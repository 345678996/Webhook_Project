package com.test.webhook.project.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.test.webhook.project.configurations.AppConstants;
import com.test.webhook.project.payloads.APIResponse;
import com.test.webhook.project.payloads.IncomingRequestDTO;
import com.test.webhook.project.payloads.IncomingRequestResponse;
import com.test.webhook.project.service.CustomUserDetails;
import com.test.webhook.project.service.IncomingRequestService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class IncomingRequestController {

    @Autowired
    private IncomingRequestService incomingRequestService;
    
    @RequestMapping(
        value = "/api/{customEndpoint}",
        method = {RequestMethod.GET,
                 RequestMethod.POST,
                 RequestMethod.PUT,
                 RequestMethod.DELETE,
                 RequestMethod.PATCH,
                 RequestMethod.OPTIONS,
                 RequestMethod.HEAD}
    )
    public ResponseEntity<IncomingRequestDTO> handleIncomingRequest(
        @PathVariable String customEndpoint,
        @RequestHeader Map<String, String> headers,
        HttpServletRequest request,
        @RequestBody(required = false) String body,
        @AuthenticationPrincipal CustomUserDetails userDetails
    )   throws JsonProcessingException {
        Long userId = userDetails.getId();
        IncomingRequestDTO requestDTO = incomingRequestService.handleIncomingRequest(customEndpoint, request, body, headers, userId);
        return new ResponseEntity<>(requestDTO, HttpStatus.OK);

    }

    @GetMapping("/api/endpoints/{endpointName}/requests")
    public ResponseEntity<IncomingRequestResponse> getIncomingRequestsByEndpointName(
        @RequestParam(name = "pageNumber", required = false) String pageNumber,
        @RequestParam(name = "pageSize", required = false) String pageSize,
        @RequestParam(name = "sortBy", required = false) String sortBy,
        @RequestParam(name = "sortOrder", required = false) String sortOrder,
        HttpServletRequest request,
        @PathVariable String endpointName,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        // Fallbacks with parsing
        int page = (pageNumber == null || pageNumber.isBlank()) ? Integer.parseInt(AppConstants.PAGE_NUMBER) : Integer.parseInt(pageNumber);
        int size = (pageSize == null || pageSize.isBlank()) ? Integer.parseInt(AppConstants.PAGE_SIZE) : Integer.parseInt(pageSize);
        String sortField = (sortBy == null || sortBy.isBlank()) ? AppConstants.SORT_ENDPOINT_BY : sortBy;
        String direction = (sortOrder == null || sortOrder.isBlank()) ? AppConstants.SORT_DIR : sortOrder;

        Long userId = userDetails.getId();
        IncomingRequestResponse response = incomingRequestService.getIncomingRequestsByEndpointName(
                                                page,
                                                size,
                                                sortField,
                                                direction,
                                                request,
                                                endpointName,
                                                userId
                                            );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/api/endpoints/{endpointName}/requests/{requestId}")
    public ResponseEntity<IncomingRequestDTO> getSingleRequestForEndpoint(
                            HttpServletRequest request,
                            @PathVariable String endpointName,
                            @PathVariable Long requestId
    ) {
        IncomingRequestDTO responseDTO = incomingRequestService.getSingleRequestForEndpoint(
                                                request,
                                                endpointName,
                                                requestId
                                            );
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/api/endpoints/{endpointName}/requests")
    public ResponseEntity<APIResponse> deleteAllRequestForEndpoint(
                            @PathVariable String endpointName,
                            HttpServletRequest request
    ) {
        incomingRequestService.deleteAllRequestForEndpoint(endpointName, request);
        return new ResponseEntity<>(new APIResponse("All request deleted for the endpoint: "+endpointName, true), HttpStatus.OK);
    }

    @DeleteMapping("/api/endpoints/{endpointName}/requests/{requestId}")
    public ResponseEntity<IncomingRequestDTO> deleteRequestForEndpoint(
                                @PathVariable String endpointName,
                                HttpServletRequest request,
                                @PathVariable Long requestId
    ) {
        IncomingRequestDTO responseDTO = incomingRequestService.deleteRequestForEndpoint(endpointName, requestId,request);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }
}
