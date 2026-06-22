package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smart.model.entity.ApiParameter;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApiParameterRepository extends JpaRepository<ApiParameter, UUID> {
    List<ApiParameter> findAllByApi_Id(UUID id);
}
