package com.test.webhook.project.service;

import com.test.webhook.project.payloads.EndpointDTO;
import com.test.webhook.project.payloads.EndpointResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface EndpointService {
    
    EndpointDTO createEndpoint(EndpointDTO endpointDTO, HttpServletRequest request);

    EndpointResponse getAllEndpoints(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, HttpServletRequest request);

    EndpointDTO searchEndpointById(Long endpointId, HttpServletRequest request);

    EndpointDTO searchEndpointByName(String endpointName, HttpServletRequest request);

    EndpointDTO deleteEndpoint(Long endpointId, HttpServletRequest request);


}
