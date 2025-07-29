package com.test.webhook.project.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.webhook.project.exceptions.APIException;
import com.test.webhook.project.exceptions.ResourceNotFoundException;
import com.test.webhook.project.model.EndpointEntity;
import com.test.webhook.project.model.IncomingRequestEntity;
import com.test.webhook.project.model.UserEntity;
import com.test.webhook.project.payloads.IncomingRequestDTO;
import com.test.webhook.project.payloads.IncomingRequestResponse;
import com.test.webhook.project.repositories.EndpointRespository;
import com.test.webhook.project.repositories.IncomingRequestRespository;
import com.test.webhook.project.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class IncomingRequestServiceImpl implements IncomingRequestService{

    private final UserRepository userRepository;

    @Autowired
    private IncomingRequestRespository incomingRequestRespository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EndpointRespository endpointRespository;

    IncomingRequestServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public IncomingRequestDTO handleIncomingRequest(String customEndpoint,
                                                    HttpServletRequest request,
                                                    String body, 
                                                    Map<String, String> headers,
                                                    Long userId) throws JsonProcessingException {
        UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        
        EndpointEntity endpoint =  user.getEndpoints().stream()
                        .filter(ep -> ep.getEndpointName().equals(customEndpoint))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", customEndpoint));

        // EndpointEntity endpoint = endpointRespository.findByEndpointName(customEndpoint)
        //             .orElseThrow(() -> new APIException("Please use a valid endpoint for sending the request"));
                    
        String method = request.getMethod();
        String headersJson = new ObjectMapper().writeValueAsString(headers);
        String queryParams = request.getQueryString();
        String path = request.getRequestURI();

        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
        }

        IncomingRequestEntity incomingRequest = IncomingRequestEntity.builder()
                            .method(method)
                            .headers(headersJson)
                            .queryParams(queryParams)
                            .path(path)
                            .ipAddress(ipAddress)
                            .body(body)
                            .receivedAt(LocalDateTime.now())
                            .endpoint(endpoint)
                            .build();
        
        endpoint.getIncomingRequests().add(incomingRequest);
        IncomingRequestEntity savedRequest = incomingRequestRespository.save(incomingRequest);
        
        IncomingRequestDTO incomingRequestDTO = modelMapper.map(savedRequest, IncomingRequestDTO.class);
        incomingRequestDTO.setEndpointId(endpoint.getEndpointId());
        incomingRequestDTO.setEndpointName(customEndpoint);

        // Deserialize headers JSON back into Map<String, String>
        Map<String, String> headersMap = new ObjectMapper().readValue(
            savedRequest.getHeaders(),
            new com.fasterxml.jackson.core.type.TypeReference<>() {}
        );
        incomingRequestDTO.setHeaders(headersMap);
        
        return incomingRequestDTO;
    }

    @Override
    public IncomingRequestResponse getIncomingRequestsByEndpointName(Integer pageNumber, Integer pageSize,
            String sortBy, String sortOrder, HttpServletRequest request, String endpointName, Long userId) {
        
        UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        
        EndpointEntity endpoint =  user.getEndpoints().stream()
                        .filter(ep -> ep.getEndpointName().equals(endpointName))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        // EndpointEntity endpoint = endpointRespository.findByEndpointName(endpointName)
        //             .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        // ----Sorting---
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending(); 
        // ----Pagenation formula----
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<IncomingRequestEntity> productPage = incomingRequestRespository.findByEndpointOrderByReceivedAtAsc(endpoint, pageDetails);

        List<IncomingRequestEntity> incomingRequests = productPage.getContent();
        if(incomingRequests.isEmpty()) {
            throw new APIException("No requests at the specific endpoint");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        List<IncomingRequestDTO> incomingRequestDTOs = incomingRequests.stream()
                .map(req -> {
                    IncomingRequestDTO dto = modelMapper.map(req, IncomingRequestDTO.class);
                    dto.setEndpointId(req.getEndpoint().getEndpointId());
                    dto.setEndpointName(req.getEndpoint().getEndpointName());
                    try {
                        Map<String, String> headersMap = objectMapper.readValue(
                            req.getHeaders(),
                            new com.fasterxml.jackson.core.type.TypeReference<>() {}
                        );
                        dto.setHeaders(headersMap);
                    } catch (JsonProcessingException e) {
                        dto.setHeaders(null);
                    }
                    return dto;
                })
                .toList();

        IncomingRequestResponse incomingRequestResponse = new IncomingRequestResponse();
        incomingRequestResponse.setContent(incomingRequestDTOs);
        incomingRequestResponse.setPageNumber(productPage.getNumber());
        incomingRequestResponse.setPageSize(productPage.getSize());
        incomingRequestResponse.setTotalElements(productPage.getTotalElements());
        incomingRequestResponse.setTotalPages(productPage.getTotalPages());
        incomingRequestResponse.setLastPage(productPage.isLast());
        
        return incomingRequestResponse;
    }

    @Override
    public IncomingRequestDTO getSingleRequestForEndpoint(HttpServletRequest request, String endpointName, Long requestId) {
        EndpointEntity endpoint = endpointRespository.findByEndpointName(endpointName)
                    .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        IncomingRequestEntity incomingRequest = incomingRequestRespository.findByRequestIdAndEndpoint(requestId, endpoint)
                    .orElseThrow(() -> new ResourceNotFoundException("Request", "requestId", requestId));

        IncomingRequestDTO incomingRequestDTO = modelMapper.map(incomingRequest, IncomingRequestDTO.class);

        // Deserialize headers JSON back into Map<String, String>
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> headersMap = objectMapper.readValue(
                incomingRequest.getHeaders(),
                new com.fasterxml.jackson.core.type.TypeReference<>() {}
            );
            incomingRequestDTO.setHeaders(headersMap);
        } catch (JsonProcessingException e) {
            incomingRequestDTO.setHeaders(null);
        }

        return incomingRequestDTO;
    }

    @Override
    public void deleteAllRequestForEndpoint(String endpointName, HttpServletRequest request) {
        EndpointEntity endpoint = endpointRespository.findByEndpointName(endpointName)
                    .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        List<IncomingRequestEntity> incomingRequests = incomingRequestRespository.findByEndpointOrderByReceivedAtAsc(endpoint);
        if(incomingRequests.isEmpty()) {
            throw new APIException("No request found at this endpoint");
        }
         
        incomingRequestRespository.deleteAll(incomingRequests);
    }

    @Override
    public IncomingRequestDTO deleteRequestForEndpoint(String endpointName, Long requestId,
            HttpServletRequest request) {
        EndpointEntity endpoint = endpointRespository.findByEndpointName(endpointName)
                    .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        IncomingRequestEntity incomingRequest = incomingRequestRespository.findById(requestId)
                            .orElseThrow(() -> new ResourceNotFoundException("Request", "requestId", requestId));
        incomingRequestRespository.delete(incomingRequest);

        IncomingRequestDTO deletedRequestDTO = modelMapper.map(incomingRequest, IncomingRequestDTO.class);
        // Deserialize headers JSON back into Map<String, String>
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, String> headersMap = objectMapper.readValue(
                incomingRequest.getHeaders(),
                new com.fasterxml.jackson.core.type.TypeReference<>() {}
            );
            deletedRequestDTO.setHeaders(headersMap);
        } catch (JsonProcessingException e) {
            deletedRequestDTO.setHeaders(null);
        }
        
        return deletedRequestDTO;
    }

}
