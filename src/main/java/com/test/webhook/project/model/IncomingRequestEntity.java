package com.test.webhook.project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "W_INCOMING_REQUEST")
public class IncomingRequestEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "r_request_id")
    private long requestId;

    @Column(name = "r_method")
    private String method;

    @Lob
    @Column(name = "r_headers", columnDefinition = "LONGTEXT")
    private String headers;

    @Lob
    @Column(name = "r_body")
    private String body;

    @Lob
    @Column(name = "r_query_params")
    private String queryParams;

    @Lob
    @Column(name = "r_path")
    private String path;

    @Column(name = "r_received_at")
    private LocalDateTime receivedAt;

    @Column(name = "r_ip_address")
    private String ipAddress;

    @ManyToOne
    @JoinColumn(name = "e_endpoint_id") // matches Endpoint's primary key column
    private EndpointEntity endpoint;
}

