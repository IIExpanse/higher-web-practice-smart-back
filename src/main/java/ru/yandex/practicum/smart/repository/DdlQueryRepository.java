package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smart.model.entity.DdlQuery;

import java.util.UUID;

@Repository
public interface DdlQueryRepository extends JpaRepository<DdlQuery, UUID> {
}
