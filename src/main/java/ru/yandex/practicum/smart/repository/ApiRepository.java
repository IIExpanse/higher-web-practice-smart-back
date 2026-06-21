package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smart.model.entity.Api;

import java.util.Optional;
import java.util.UUID;

public interface ApiRepository extends JpaRepository<Api, UUID> {
    Optional<Api> findByMethodAndPath(String method, String path);
}
