package ru.yandex.practicum.smart.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.smart.dao.DynamicJdbcDao;
import ru.yandex.practicum.smart.exception.HttpServerException;
import ru.yandex.practicum.smart.model.entity.Api;
import ru.yandex.practicum.smart.model.entity.ApiParameter;
import ru.yandex.practicum.smart.model.entity.ApiResult;
import ru.yandex.practicum.smart.model.entity.DmlQuery;
import ru.yandex.practicum.smart.repository.ApiParameterRepository;
import ru.yandex.practicum.smart.repository.ApiRepository;
import ru.yandex.practicum.smart.repository.ApiResultRepository;
import ru.yandex.practicum.smart.repository.DmlQueryRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class DynamicRequestServiceImplTest {

    @Mock
    private ApiRepository apiRepository;

    @Mock
    private DmlQueryRepository dmlQueryRepository;

    @Mock
    private DynamicJdbcDao dynamicJdbcDao;

    @Mock
    private ApiParameterRepository apiParameterRepository;

    @Mock
    private ApiResultRepository apiResultRepository;

    @InjectMocks
    private DynamicRequestServiceImpl dynamicRequestService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void handleDynamicRequest_Successful() {
        // Given
        String path = "/api/users";
        String method = "GET";
        Map<String, String> parameters = Map.of("page", "1", "limit", "10");

        Api api = new Api();
        api.setId(UUID.randomUUID());

        DmlQuery dmlQuery = new DmlQuery();
        dmlQuery.setId(UUID.randomUUID());
        dmlQuery.setApi(api);
        dmlQuery.setQuery("SELECT * FROM users WHERE page = :page AND limit = :limit");

        ApiParameter pageParam = new ApiParameter();
        pageParam.setName("page");
        ApiParameter limitParam = new ApiParameter();
        limitParam.setName("limit");

        ApiResult idResult = new ApiResult();
        idResult.setName("id");
        ApiResult nameResult = new ApiResult();
        nameResult.setName("name");

        when(apiRepository.findByMethodAndPath(eq(method), eq(path))).thenReturn(Optional.of(api));
        when(dmlQueryRepository.findFirstByApi_Id(eq(api.getId()))).thenReturn(Optional.of(dmlQuery));
        when(apiParameterRepository.findAllByApi_Id(eq(api.getId())))
                .thenReturn(List.of(pageParam, limitParam));
        when(apiResultRepository.findAllByApi_Id(eq(api.getId())))
                .thenReturn(List.of(idResult, nameResult));
        when(dynamicJdbcDao.executeDynamicQuery(eq(dmlQuery.getQuery()), any(), any()))
                .thenReturn(List.of(Map.of("id", "1", "name", "test")));

        // When
        List<Map<String, String>> result = dynamicRequestService.handleDynamicRequest(path, method, parameters);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("1", result.get(0).get("id"));
        assertEquals("test", result.get(0).get("name"));
        verify(dynamicJdbcDao, times(1)).executeDynamicQuery(anyString(), any(), any());
    }

    @Test
    void handleDynamicRequest_ApiNotFound() {
        // Given
        String path = "/api/users";
        String method = "GET";
        Map<String, String> parameters = Map.of("page", "1");

        when(apiRepository.findByMethodAndPath(eq(method), eq(path))).thenReturn(Optional.empty());

        // When & Then
        HttpServerException exception = assertThrows(HttpServerException.class, () -> {
            dynamicRequestService.handleDynamicRequest(path, method, parameters);
        });
        assertTrue(exception.getMessage().contains("Api for method GET and path /api/users was not found in database"));
    }

    @Test
    void handleDynamicRequest_DmlQueryNotFound() {
        // Given
        String path = "/api/users";
        String method = "GET";
        Map<String, String> parameters = Map.of("page", "1");

        Api api = new Api();
        api.setId(UUID.randomUUID());

        when(apiRepository.findByMethodAndPath(eq(method), eq(path))).thenReturn(Optional.of(api));
        when(dmlQueryRepository.findFirstByApi_Id(eq(api.getId()))).thenReturn(Optional.empty());

        // When
        List<Map<String, String>> result = dynamicRequestService.handleDynamicRequest(path, method, parameters);

        // Then
        assertNotNull(result);
        assertEquals("bound query for invoked api does not exist", result.get(0).get("message"));
    }

    @Test
    void handleDynamicRequest_ParameterFiltering() {
        // Given
        String path = "/api/users";
        String method = "GET";
        Map<String, String> parameters = Map.of("page", "1", "limit", "10", "extra", "value");

        Api api = new Api();
        api.setId(UUID.randomUUID());

        ApiParameter pageParam = new ApiParameter();
        pageParam.setName("page");

        DmlQuery dmlQuery = new DmlQuery();
        dmlQuery.setId(UUID.randomUUID());
        dmlQuery.setApi(api);
        dmlQuery.setQuery("SELECT * FROM users");

        when(apiRepository.findByMethodAndPath(eq(method), eq(path))).thenReturn(Optional.of(api));
        when(dmlQueryRepository.findFirstByApi_Id(eq(api.getId()))).thenReturn(Optional.of(dmlQuery));
        when(apiParameterRepository.findAllByApi_Id(eq(api.getId())))
                .thenReturn(List.of(pageParam));
        when(apiResultRepository.findAllByApi_Id(eq(api.getId()))).thenReturn(List.of());
        when(dynamicJdbcDao.executeDynamicQuery(anyString(), any(), any()))
                .thenReturn(List.of());

        // When
        dynamicRequestService.handleDynamicRequest(path, method, parameters);

        // Then
        verify(dynamicJdbcDao, times(1)).executeDynamicQuery(anyString(), any(), argThat(map -> {
            assertTrue(map.containsKey("page"));
            assertFalse(map.containsKey("limit"));
            assertFalse(map.containsKey("extra"));
            return true;
        }));
    }
}
