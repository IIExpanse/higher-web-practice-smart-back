package ru.yandex.practicum.smart.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "message", schema = "smart_backend")
public class Message {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "role", nullable = false)
    private String role;

    @Column(name = "number", nullable = false)
    private Integer number;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "extracted_content")
    private String extractedContent;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
