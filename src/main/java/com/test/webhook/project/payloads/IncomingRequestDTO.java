package com.test.webhook.project.payloads;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomingRequestDTO {
    private Long id;
    private String method;
    private Map<String, String> headers;       
    private String body;         
    private String queryParams;  
    private String path;         
    private LocalDateTime receivedAt;
    private String ipAddress;
    private Long endpointId;     
    private String endpointName;
}
