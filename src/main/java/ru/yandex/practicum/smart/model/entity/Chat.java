package ru.yandex.practicum.smart.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "chat", schema = "smart_backend")
public class Chat {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "system_prompt", nullable = false)
    private String systemPrompt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
