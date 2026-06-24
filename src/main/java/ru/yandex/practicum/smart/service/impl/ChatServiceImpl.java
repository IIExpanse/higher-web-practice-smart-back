package ru.yandex.practicum.smart.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.smart.client.OllamaClient;
import ru.yandex.practicum.smart.dto.ChatRequest;
import ru.yandex.practicum.smart.dto.ChatResponse;
import ru.yandex.practicum.smart.dto.OllamaMessage;
import ru.yandex.practicum.smart.dto.OllamaResponse;
import ru.yandex.practicum.smart.exception.HttpClientException;
import ru.yandex.practicum.smart.exception.OllamaResponseParsingException;
import ru.yandex.practicum.smart.model.entity.Chat;
import ru.yandex.practicum.smart.model.entity.Message;
import ru.yandex.practicum.smart.repository.ChatRepository;
import ru.yandex.practicum.smart.repository.MessageRepository;
import ru.yandex.practicum.smart.service.ChatService;
import ru.yandex.practicum.smart.utils.Constants;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final OllamaClient ollamaClient;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final ObjectMapper objectMapper;

    @Value("${smart-back.ollama.api.max-attempts}")
    private int maxAttempts;

    @Override
    public ChatResponse sendMessage(ChatRequest request) {
        if (log.isDebugEnabled()) {
            log.debug("Received a request to send a message with chatId {} and query {}",
                    request.getChatId(), request.getMessage()
            );
        }
        List<OllamaMessage> ollamaMessages = new ArrayList<>();
        Chat chat = resolveChat(ollamaMessages, request);

        Message messageToSend = getMessageTemplate(chat, Constants.ROLE_USER);
        messageToSend.setNumber(ollamaMessages.size());
        messageToSend.setContent(request.getMessage());

        messageRepository.save(messageToSend);
        ollamaMessages.add(new OllamaMessage(Constants.ROLE_USER, messageToSend.getContent()));

        int attempts = 0;

        while (attempts < maxAttempts) {
            OllamaResponse response = ollamaClient.sendMessage(ollamaMessages);

            Message responseMessage = getMessageTemplate(chat, Constants.ROLE_ASSISTANT);
            responseMessage.setRole(Constants.ROLE_ASSISTANT);
            responseMessage.setNumber(messageToSend.getNumber() + 1);
            responseMessage.setContent(response.getMessage().getContent());

            try {
                String json = extractJsonPart(response.getMessage().getContent());
                responseMessage.setExtractedContent(json);
                messageRepository.save(responseMessage);

                log.info("Parsed response successfully");
                return new ChatResponse(chat.getId(), response.getMessage().getContent(), json);

            } catch (OllamaResponseParsingException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Could not parse response message: {}", response.getMessage().getContent(), e);
                }
                messageRepository.save(responseMessage);
                ollamaMessages.add(response.getMessage());

                messageToSend = getMessageTemplate(chat, Constants.ROLE_USER);
                messageToSend.setNumber(responseMessage.getNumber() + 1);

                String errorText = "There was an error parsing your response. " +
                        "Fix it and try again. Error:  " + e.getMessage();
                messageToSend.setContent(errorText);
                OllamaMessage errorMessage = new OllamaMessage(Constants.ROLE_USER, errorText);
                ollamaMessages.add(errorMessage);

                messageRepository.save(messageToSend);
                attempts++;
            }
        }
        throw new OllamaResponseParsingException("Could not parse response markdown and json message. " +
                "Try to reformulate your query."
        );
    }

    private Chat resolveChat(List<OllamaMessage> ollamaMessages, ChatRequest request) {
        Chat chat;

        if (request.getChatId() != null) {
            Optional<Chat> chatOptional = chatRepository.findById(request.getChatId());
            if (chatOptional.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("Chat with id {} not found", request.getChatId());
                }
                throw new HttpClientException(String.format("Chat with id %s does not exist", request.getChatId()));
            }
            chat = chatOptional.get();

            ollamaMessages.add(new OllamaMessage(Constants.ROLE_SYSTEM, chat.getSystemPrompt()));
            ollamaMessages.addAll(messageRepository.findByChatIdOrderByNumberAsc(request.getChatId()).stream()
                    .map(message -> new OllamaMessage(message.getRole(), message.getContent()))
                    .collect(Collectors.toList())
            );

        } else {
            chat = new Chat();
            chat.setId(UUID.randomUUID());
            chat.setCreatedAt(Instant.now());
            chat.setSystemPrompt(Constants.SYSTEM_PROMPT);
            chatRepository.save(chat);

            log.info("Created new chat with id {}.", chat.getId());

            ollamaMessages.add(new OllamaMessage(Constants.ROLE_SYSTEM, chat.getSystemPrompt()));
        }
        return chat;
    }

    private Message getMessageTemplate(Chat chat, String role) {
        Message messageToSend = new Message();
        messageToSend.setId(UUID.randomUUID());
        messageToSend.setChat(chat);
        messageToSend.setRole(role);
        messageToSend.setCreatedAt(Instant.now());
        return messageToSend;
    }

    private String extractJsonPart(String message) {
        try {
            if (message == null || message.isEmpty()) {
                return null;
            }

            // 1. Создаем парсер с базовыми настройками
            MutableDataSet options = new MutableDataSet();
            Parser parser = Parser.builder(options).build();

            // 2. Строим дерево элементов (AST)
            Node document = parser.parse(message);
            AtomicReference<String> jsonText = new AtomicReference<>();
            AtomicInteger counter = new AtomicInteger();

            // 3. Создаем посетителя для поиска блоков кода с типом ```json
            NodeVisitor visitor = new NodeVisitor(
                    new VisitHandler<>(FencedCodeBlock.class, block -> {
                        // Проверяем, что это именно JSON блок
                        if (block.getInfo().matches("json")) {
                            // Извлекаем чистый текст внутри блока кода
                            jsonText.set(block.getContentChars().toString());
                            counter.incrementAndGet();
                        }
                    })
            );

            // 4. Запускаем обход дерева
            visitor.visit(document);

            if (counter.get() == 0) {
                throw new OllamaResponseParsingException("Provided markdown doesn't contain any json block.");
            }
            if (counter.get() > 1) {
                throw new OllamaResponseParsingException("Provided markdown contains more than one json block.");
            }
            objectMapper.readTree(jsonText.get());

            return jsonText.get();

        } catch (Exception e) {
            throw new OllamaResponseParsingException("Failed to extract JSON from markdown: " + e.getMessage());
        }
    }
}
