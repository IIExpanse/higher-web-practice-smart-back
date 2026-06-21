package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.smart.model.entity.DdlQuery;

import java.util.UUID;

public interface DdlQueryRepository extends JpaRepository<DdlQuery, UUID> {
}
