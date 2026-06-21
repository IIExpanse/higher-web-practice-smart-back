package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smart.model.entity.DmlQuery;

import java.util.UUID;

public interface DmlQueryRepository extends JpaRepository<DmlQuery, UUID> {
}
