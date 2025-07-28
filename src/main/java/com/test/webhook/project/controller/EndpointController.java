package com.test.webhook.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.test.webhook.project.configurations.AppConstants;
import com.test.webhook.project.payloads.EndpointDTO;
import com.test.webhook.project.payloads.EndpointResponse;
import com.test.webhook.project.service.CustomUserDetails;
import com.test.webhook.project.service.EndpointService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class EndpointController {

    @Autowired
    private EndpointService endpointService;

    @PostMapping("/api/endpoints")
    public ResponseEntity<EndpointDTO> createEndpoint(@RequestBody EndpointDTO endpointDTO,
                                                      @AuthenticationPrincipal CustomUserDetails userDetails,
                                                       HttpServletRequest request) {
        Long userId = userDetails.getId();
        EndpointDTO savedEndpointDTO = endpointService.createEndpoint(endpointDTO, request, userId);
        return new ResponseEntity<>(savedEndpointDTO, HttpStatus.CREATED);
    }

    @GetMapping("/api/endpoints")
    public ResponseEntity<EndpointResponse> getAllEndpoints(
        @RequestParam(name = "pageNumber", required = false) String pageNumber,
        @RequestParam(name = "pageSize", required = false) String pageSize,
        @RequestParam(name = "sortBy", required = false) String sortBy,
        @RequestParam(name = "sortOrder", required = false) String sortOrder,
        @AuthenticationPrincipal CustomUserDetails userDetails,
        HttpServletRequest request
    ) {
        // Fallbacks with parsing
        int page = (pageNumber == null || pageNumber.isBlank()) ? Integer.parseInt(AppConstants.PAGE_NUMBER) : Integer.parseInt(pageNumber);
        int size = (pageSize == null || pageSize.isBlank()) ? Integer.parseInt(AppConstants.PAGE_SIZE) : Integer.parseInt(pageSize);
        String sortField = (sortBy == null || sortBy.isBlank()) ? AppConstants.SORT_ENDPOINT_BY : sortBy;
        String direction = (sortOrder == null || sortOrder.isBlank()) ? AppConstants.SORT_DIR : sortOrder;

        Long userId = userDetails.getId();
        EndpointResponse endpointResponse = endpointService.getAllEndpoints(page, size, sortField, direction, request, userId);

        return new ResponseEntity<>(endpointResponse, HttpStatus.OK);
    }

    @GetMapping("/api/endpoints/{endpointId}")
    public ResponseEntity<EndpointDTO> searchEndpointById(@PathVariable Long endpointId, HttpServletRequest request) {
        EndpointDTO endpointDTO = endpointService.searchEndpointById(endpointId,request);
        return new ResponseEntity<>(endpointDTO, HttpStatus.OK);
    }

    @GetMapping("/api/endpoints/name/{endpointName}")
    public ResponseEntity<EndpointDTO> searchEndpointByName(@PathVariable String endpointName, HttpServletRequest request) {
        EndpointDTO endpointDTO = endpointService.searchEndpointByName(endpointName,request);
        return new ResponseEntity<>(endpointDTO, HttpStatus.OK);
    }

    @DeleteMapping("/api/endpoints/{endpointId}")
    public ResponseEntity<EndpointDTO> deleteEndpoint(@PathVariable Long endpointId, HttpServletRequest request) {
        EndpointDTO endpointDTO = endpointService.deleteEndpoint(endpointId, request);
        return new ResponseEntity<>(endpointDTO, HttpStatus.OK);
    }


}
