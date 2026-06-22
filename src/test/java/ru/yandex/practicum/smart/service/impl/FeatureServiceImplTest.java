package ru.yandex.practicum.smart.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.yandex.practicum.smart.dto.FeatureRequest;
import ru.yandex.practicum.smart.dto.FeatureResponse;
import ru.yandex.practicum.smart.model.entity.Feature;
import ru.yandex.practicum.smart.repository.FeatureRepository;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FeatureServiceImplTest {

    @Mock
    private FeatureRepository featureRepository;

    @InjectMocks
    private FeatureServiceImpl featureService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveFeature_Successful() {
        // Given
        FeatureRequest request = new FeatureRequest();
        request.setName("Test Feature");

        Feature savedFeature = new Feature();
        savedFeature.setId(UUID.randomUUID());
        savedFeature.setName(request.getName());
        savedFeature.setCreatedAt(Instant.now());

        when(featureRepository.save(any(Feature.class))).thenReturn(savedFeature);

        // When
        FeatureResponse response = featureService.saveFeature(request);

        // Then
        assertNotNull(response);
        assertEquals(request.getName(), response.getName());
        assertNotNull(response.getFeatureId());
        assertNotNull(response.getCreatedAt());
        verify(featureRepository, times(1)).save(any(Feature.class));
    }
}
