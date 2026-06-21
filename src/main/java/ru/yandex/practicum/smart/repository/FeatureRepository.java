package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smart.model.entity.Feature;

import java.util.UUID;

public interface FeatureRepository extends JpaRepository<Feature, UUID> {
}
