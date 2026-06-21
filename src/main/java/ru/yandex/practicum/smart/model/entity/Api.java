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
@Table(name = "api", schema = "smart_backend")
public class Api {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @Column(name = "method", nullable = false)
    private String method;

    @Column(name = "path", nullable = false, length = 100)
    private String path;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
