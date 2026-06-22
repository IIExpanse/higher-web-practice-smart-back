package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smart.model.entity.ApiResult;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApiResultRepository extends JpaRepository<ApiResult, UUID> {
    List<ApiResult> findAllByApi_Id(UUID id);
}
