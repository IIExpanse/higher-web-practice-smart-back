package ru.yandex.practicum.smart.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class DbQueryGenerationServiceImplTest {

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private DdlQueryRepository ddlQueryRepository;

    @Mock
    private DmlQueryRepository dmlQueryRepository;

    @Mock
    private DynamicJdbcDao dynamicJdbcDao;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private DbQueryGenerationServiceImpl dbQueryGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generateDmlQuery_Successful() throws Exception {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID apiId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        DmlQueryGenerationRequest request = new DmlQueryGenerationRequest();
        request.setChatId(chatId);
        request.setApiId(apiId);

        Chat chat = new Chat();
        chat.setId(chatId);

        Api api = new Api();
        api.setId(apiId);

        Message message = new Message();
        message.setId(messageId);
        message.setChat(chat);
        message.setExtractedContent("{\"query\":\"INSERT INTO users VALUES (1, 'test')\"}");

        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setQuery("INSERT INTO users VALUES (1, 'test')");

        DmlQuery savedDmlQuery = new DmlQuery();
        savedDmlQuery.setId(UUID.randomUUID());
        savedDmlQuery.setApi(api);
        savedDmlQuery.setQuery(sqlQuery.getQuery());

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(apiRepository.findById(apiId)).thenReturn(Optional.of(api));
        when(messageRepository.findFirstByChat_IdAndExtractedContentNotNullOrderByNumberDesc(chatId))
                .thenReturn(Optional.of(message));
        when(objectMapper.readValue(anyString(), eq(SqlQuery.class))).thenReturn(sqlQuery);

        // When
        dbQueryGenerationService.generateDmlQuery(request);

        // Then
        verify(dmlQueryRepository, times(1)).save(any(DmlQuery.class));
    }

    @Test
    void generateDmlQuery_ChatNotFound() {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID apiId = UUID.randomUUID();
        DmlQueryGenerationRequest request = new DmlQueryGenerationRequest();
        request.setChatId(chatId);
        request.setApiId(apiId);

        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        // When & Then
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            dbQueryGenerationService.generateDmlQuery(request);
        });
        assertEquals(String.format("Chat with id %s does not exist", chatId), exception.getMessage());
    }

    @Test
    void generateDmlQuery_ApiNotFound() {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID apiId = UUID.randomUUID();
        DmlQueryGenerationRequest request = new DmlQueryGenerationRequest();
        request.setChatId(chatId);
        request.setApiId(apiId);

        Chat chat = new Chat();
        chat.setId(chatId);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(apiRepository.findById(apiId)).thenReturn(Optional.empty());

        // When & Then
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            dbQueryGenerationService.generateDmlQuery(request);
        });
        assertEquals(String.format("Api with id %s does not exist", apiId), exception.getMessage());
    }

    @Test
    void generateDdlQuery_Successful() throws Exception {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID featureId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();

        DdlQueryGenerationRequest request = new DdlQueryGenerationRequest();
        request.setChatId(chatId);
        request.setFeatureId(featureId);

        Chat chat = new Chat();
        chat.setId(chatId);

        Feature feature = new Feature();
        feature.setId(featureId);

        Message message = new Message();
        message.setId(messageId);
        message.setChat(chat);
        message.setExtractedContent("{\"query\":\"CREATE TABLE users (id INT, name VARCHAR(100))\"}");

        SqlQuery sqlQuery = new SqlQuery();
        sqlQuery.setQuery("CREATE TABLE users (id INT, name VARCHAR(100))");

        DdlQuery savedDdlQuery = new DdlQuery();
        savedDdlQuery.setId(UUID.randomUUID());
        savedDdlQuery.setFeature(feature);
        savedDdlQuery.setQuery(sqlQuery.getQuery());

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(featureRepository.findById(featureId)).thenReturn(Optional.of(feature));
        when(messageRepository.findFirstByChat_IdAndExtractedContentNotNullOrderByNumberDesc(chatId))
                .thenReturn(Optional.of(message));
        when(ddlQueryRepository.save(any(DdlQuery.class))).thenReturn(savedDdlQuery);
        when(objectMapper.readValue(anyString(), eq(SqlQuery.class))).thenReturn(sqlQuery);

        // When
        dbQueryGenerationService.generateDdlQuery(request);

        // Then
        verify(ddlQueryRepository, times(1)).save(any(DdlQuery.class));
        verify(dynamicJdbcDao, times(1)).executeModifyingQuery(anyString());
    }

    @Test
    void generateDdlQuery_FeatureNotFound() {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID featureId = UUID.randomUUID();
        DdlQueryGenerationRequest request = new DdlQueryGenerationRequest();
        request.setChatId(chatId);
        request.setFeatureId(featureId);

        when(chatRepository.findById(chatId)).thenReturn(Optional.of(new Chat()));
        when(featureRepository.findById(featureId)).thenReturn(Optional.empty());

        // When & Then
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            dbQueryGenerationService.generateDdlQuery(request);
        });
        assertEquals(String.format("Feature with id %s does not exist", featureId), exception.getMessage());
    }
}
