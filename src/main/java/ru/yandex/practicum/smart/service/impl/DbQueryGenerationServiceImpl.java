package ru.yandex.practicum.smart.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.smart.dao.DynamicJdbcDao;
import ru.yandex.practicum.smart.dto.DdlQueryGenerationRequest;
import ru.yandex.practicum.smart.dto.DmlQueryGenerationRequest;
import ru.yandex.practicum.smart.dto.SqlQuery;
import ru.yandex.practicum.smart.exception.HttpClientException;
import ru.yandex.practicum.smart.model.entity.Api;
import ru.yandex.practicum.smart.model.entity.Chat;
import ru.yandex.practicum.smart.model.entity.DdlQuery;
import ru.yandex.practicum.smart.model.entity.DmlQuery;
import ru.yandex.practicum.smart.model.entity.Feature;
import ru.yandex.practicum.smart.model.entity.Message;
import ru.yandex.practicum.smart.repository.ApiRepository;
import ru.yandex.practicum.smart.repository.ChatRepository;
import ru.yandex.practicum.smart.repository.DdlQueryRepository;
import ru.yandex.practicum.smart.repository.DmlQueryRepository;
import ru.yandex.practicum.smart.repository.FeatureRepository;
import ru.yandex.practicum.smart.repository.MessageRepository;
import ru.yandex.practicum.smart.service.DbQueryGenerationService;
import ru.yandex.practicum.smart.validator.SqlValidator;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbQueryGenerationServiceImpl implements DbQueryGenerationService {
    private final FeatureRepository featureRepository;
    private final ChatRepository chatRepository;
    private final ApiRepository apiRepository;
    private final MessageRepository messageRepository;
    private final DdlQueryRepository ddlQueryRepository;
    private final DmlQueryRepository dmlQueryRepository;
    private final SqlValidator sqlValidator;
    private final ObjectMapper objectMapper;
    private final DynamicJdbcDao dynamicJdbcDao;

    @Override
    public void generateDmlQuery(DmlQueryGenerationRequest request) {
        Chat chat = chatRepository.findById(request.getChatId()).orElse(null);
        if (chat == null) {
            throw new HttpClientException(String.format("Chat with id %s does not exist", request.getChatId()));
        }
        Api api = apiRepository.findById(request.getApiId()).orElse(null);
        if (api == null) {
            throw new HttpClientException(String.format("Api with id %s does not exist", request.getApiId()));
        }

        SqlQuery sqlQuery = getSqlQuery(chat);

        DmlQuery dmlQuery = new DmlQuery();
        dmlQuery.setId(UUID.randomUUID());
        dmlQuery.setApi(api);
        dmlQuery.setCreatedAt(Instant.now());
        dmlQuery.setQuery(sqlQuery.getQuery());
        dmlQueryRepository.save(dmlQuery);
    }

    @Override
    @Transactional
    public void generateDdlQuery(DdlQueryGenerationRequest request) {
        Chat chat = chatRepository.findById(request.getChatId()).orElse(null);
        if (chat == null) {
            throw new HttpClientException(String.format("Chat with id %s does not exist", request.getChatId()));
        }
        Feature feature = featureRepository.findById(request.getFeatureId()).orElse(null);
        if (feature == null) {
            throw new HttpClientException(String.format("Feature with id %s does not exist", request.getFeatureId()));
        }

        SqlQuery sqlQuery = getSqlQuery(chat);

        DdlQuery ddlQuery = new DdlQuery();
        ddlQuery.setId(UUID.randomUUID());
        ddlQuery.setFeature(feature);
        ddlQuery.setCreatedAt(Instant.now());
        ddlQuery.setQuery(sqlQuery.getQuery());
        ddlQueryRepository.save(ddlQuery);

        dynamicJdbcDao.executeModifyingQuery(sqlQuery.getQuery());
        log.debug("Successfully executed modifying query {}", sqlQuery.getQuery());
    }

    private SqlQuery getSqlQuery(Chat chat) {
        Message message = messageRepository.findFirstByChat_IdAndExtractedContentNotNullOrderByNumberDesc(chat.getId())
                .orElse(null);
        if (message == null) {
            throw new HttpClientException("Failed to find chat message with non-empty extracted content");
        }
        SqlQuery sqlQuery = parseSqlQuery(message.getExtractedContent()).orElse(null);
        if (sqlQuery == null) {
            throw new HttpClientException(String.format("Extracted content from last message with id=%s " +
                    "with chatId=%s cannot ba parsed as SqlQuery", message.getId(), message.getChat().getId()));
        }
        if (!sqlValidator.isValidSql(sqlQuery.getQuery())) {
            throw new HttpClientException("Sql query from extracted content is not valid sql.");
        }
        return sqlQuery;
    }


    private Optional<SqlQuery> parseSqlQuery(String extractedContent) {
        try {
            SqlQuery config = objectMapper.readValue(extractedContent, SqlQuery.class);
            return Optional.of(config);

        } catch (JsonProcessingException e) {
            log.debug("Failed to parse SqlQuery from extracted content: {}", extractedContent);
            return Optional.empty();
        }
    }
}
