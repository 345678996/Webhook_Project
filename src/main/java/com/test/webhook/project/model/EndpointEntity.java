package com.test.webhook.project.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "W_ENDPOINT")
public class EndpointEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "e_endpoint_id")
    private Long endpointId;

    @Column(name = "e_endpoint_name", nullable = false, unique = true)
    private String endpointName;

    @Column(name = "e_description", nullable = false)
    private String description;

    @CreationTimestamp
    @Column(name = "e_created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "endpoint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncomingRequestEntity> incomingRequests;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "u_user_id") // FK column in W_ENDPOINT table
    private UserEntity user;


    @PrePersist
    public void prePersistEndpoint() {
        if (endpointName == null) {
            endpointName = UUID.randomUUID().toString();
        }
        if (description == null) {
            description = "test endpoint";
        }
    }
}

