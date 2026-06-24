package ru.yandex.practicum.smart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.smart.model.entity.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    List<Message> findByChatIdOrderByNumberAsc(UUID chatId);

    Optional<Message> findFirstByChat_IdAndExtractedContentNotNullOrderByNumberDesc(UUID chatId);
}
