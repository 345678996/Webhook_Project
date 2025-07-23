package com.test.webhook.project.service;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.webhook.project.payloads.IncomingRequestDTO;
import com.test.webhook.project.payloads.IncomingRequestResponse;

import jakarta.servlet.http.HttpServletRequest;

public interface IncomingRequestService {
    
    IncomingRequestDTO handleIncomingRequest(String customEndpoint, HttpServletRequest request, String body, Map<String, String> headers) throws JsonProcessingException;

    IncomingRequestResponse getIncomingRequestsByEndpointName(Integer pageNumber, Integer pageSize, String sortBy,
            String sortOrder, HttpServletRequest request, String endpointName);

    IncomingRequestDTO getSingleRequestForEndpoint(HttpServletRequest request, String endpointName, Long requestId);

    void deleteAllRequestForEndpoint(String endpointName, HttpServletRequest request);

    IncomingRequestDTO deleteRequestForEndpoint(String endpointName, Long requestId, HttpServletRequest request);

}
