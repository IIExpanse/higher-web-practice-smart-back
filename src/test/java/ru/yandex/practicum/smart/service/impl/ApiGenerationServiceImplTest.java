package ru.yandex.practicum.smart.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.smart.dto.ApiConfig;
import ru.yandex.practicum.smart.dto.ApiGenerationRequest;
import ru.yandex.practicum.smart.dto.ApiGenerationResponse;
import ru.yandex.practicum.smart.dto.Config;
import ru.yandex.practicum.smart.exception.HttpClientException;
import ru.yandex.practicum.smart.model.entity.Api;
import ru.yandex.practicum.smart.model.entity.ApiParameter;
import ru.yandex.practicum.smart.model.entity.ApiResult;
import ru.yandex.practicum.smart.model.entity.Chat;
import ru.yandex.practicum.smart.model.entity.Feature;
import ru.yandex.practicum.smart.model.entity.Message;
import ru.yandex.practicum.smart.repository.ApiParameterRepository;
import ru.yandex.practicum.smart.repository.ApiRepository;
import ru.yandex.practicum.smart.repository.ApiResultRepository;
import ru.yandex.practicum.smart.repository.ChatRepository;
import ru.yandex.practicum.smart.repository.FeatureRepository;
import ru.yandex.practicum.smart.repository.MessageRepository;
import ru.yandex.practicum.smart.service.DynamicRouteService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ApiGenerationServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private DynamicRouteService dynamicRouteService;

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private ApiParameterRepository apiParameterRepository;

    @Mock
    private ApiResultRepository apiResultRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ApiGenerationServiceImpl apiGenerationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void generate_Successful() throws Exception {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID featureId = UUID.randomUUID();
        UUID messageId = UUID.randomUUID();
        UUID apiId = UUID.randomUUID();

        ApiGenerationRequest request = new ApiGenerationRequest();
        request.setChatId(chatId);
        request.setFeatureId(featureId);

        Feature feature = new Feature();
        feature.setId(featureId);

        Chat chat = new Chat();
        chat.setId(chatId);

        Message message = new Message();
        message.setId(messageId);
        message.setChat(chat);
        message.setExtractedContent("{\"config\":{\"method\":\"GET\",\"url\":\"/api/users\",\"parameters\":[\"page\"],\"results\":[\"id\"]}}");

        ApiConfig config = new ApiConfig();
        config.setMethod("GET");
        config.setUrl("/api/users");
        config.setParameters(List.of("page"));
        config.setResults(List.of("id"));

        Config wrapperConfig = new Config();
        wrapperConfig.setConfig(config);

        Api savedApi = new Api();
        savedApi.setId(apiId);
        savedApi.setFeature(feature);
        savedApi.setMethod("GET");
        savedApi.setPath("/api/users");

        ApiParameter pageParam = new ApiParameter();
        pageParam.setId(UUID.randomUUID());
        pageParam.setApi(savedApi);
        pageParam.setName("page");
        pageParam.setCreatedAt(Instant.now());

        ApiResult idResult = new ApiResult();
        idResult.setId(UUID.randomUUID());
        idResult.setApi(savedApi);
        idResult.setName("id");
        idResult.setCreatedAt(Instant.now());

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(feature));
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(messageRepository.findFirstByChat_IdAndExtractedContentNotNullOrderByNumberDesc(chatId))
                .thenReturn(Optional.of(message));
        when(apiRepository.save(any(Api.class))).thenReturn(savedApi);
        when(objectMapper.readValue(anyString(), eq(Config.class))).thenReturn(wrapperConfig);
        when(apiParameterRepository.saveAll(anyList())).thenReturn(List.of(pageParam));
        when(apiResultRepository.saveAll(anyList())).thenReturn(List.of(idResult));

        // When
        ApiGenerationResponse response = apiGenerationService.generate(request);

        // Then
        assertNotNull(response);
        assertEquals(request.getChatId(), response.getChatId());
        assertEquals(request.getFeatureId(), response.getFeatureId());
        assertEquals(apiId, response.getApiId());
        verify(apiRepository, times(1)).save(any(Api.class));
        verify(dynamicRouteService, times(1)).registerUrl(anyString(), any());
        verify(apiParameterRepository, times(1)).saveAll(anyList());
        verify(apiResultRepository, times(1)).saveAll(anyList());
    }

    @Test
    void generate_FeatureNotFound() {
        // Given
        UUID featureId = UUID.randomUUID();
        ApiGenerationRequest request = new ApiGenerationRequest();
        request.setFeatureId(featureId);

        when(featureRepository.findById(featureId)).thenReturn(Optional.empty());

        // When & Then
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            apiGenerationService.generate(request);
        });
        assertEquals("Feature not found by id", exception.getMessage());
    }

    @Test
    void generate_ChatNotFound() {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID featureId = UUID.randomUUID();
        ApiGenerationRequest request = new ApiGenerationRequest();
        request.setChatId(chatId);
        request.setFeatureId(featureId);

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(new Feature()));
        when(chatRepository.findById(chatId)).thenReturn(Optional.empty());

        // When & Then
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            apiGenerationService.generate(request);
        });
        assertEquals(String.format("Chat with id %s does not exist", chatId), exception.getMessage());
    }

    @Test
    void generate_MessageNotFound() {
        // Given
        UUID chatId = UUID.randomUUID();
        UUID featureId = UUID.randomUUID();
        ApiGenerationRequest request = new ApiGenerationRequest();
        request.setChatId(chatId);
        request.setFeatureId(featureId);

        Feature feature = new Feature();
        feature.setId(featureId);

        Chat chat = new Chat();
        chat.setId(chatId);

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(feature));
        when(chatRepository.findById(chatId)).thenReturn(Optional.of(chat));
        when(messageRepository.findFirstByChat_IdAndExtractedContentNotNullOrderByNumberDesc(chatId))
                .thenReturn(Optional.empty());

        // When & Then
        HttpClientException exception = assertThrows(HttpClientException.class, () -> {
            apiGenerationService.generate(request);
        });
        assertEquals("Failed to find chat message with non-empty extracted content", exception.getMessage());
    }
}
