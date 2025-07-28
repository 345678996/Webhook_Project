package com.test.webhook.project.payloads;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EndpointDTO {
    private Long endpointId;
    private String endpointName;
    private String description;

    private String customEndpointUrl;

}
