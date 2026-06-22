package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smart.model.entity.DmlQuery;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface DmlQueryRepository extends JpaRepository<DmlQuery, UUID> {
    Optional<DmlQuery> findFirstByApi_Id(UUID apiId);
}
