package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smart.model.entity.ApiParameter;

import java.util.UUID;

public interface ApiParameterRepository extends JpaRepository<ApiParameter, UUID> {
}
