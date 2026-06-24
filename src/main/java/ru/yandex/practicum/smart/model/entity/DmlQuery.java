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
@Table(name = "dml_query", schema = "smart_backend")
public class DmlQuery {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "api_id", nullable = false)
    private Api api;

    @Column(name = "query", nullable = false)
    private String query;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
