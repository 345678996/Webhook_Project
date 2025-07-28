package com.test.webhook.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.test.webhook.project.configurations.Mapper;
import com.test.webhook.project.exceptions.APIException;
import com.test.webhook.project.exceptions.ResourceNotFoundException;
import com.test.webhook.project.model.EndpointEntity;
import com.test.webhook.project.model.UserEntity;
import com.test.webhook.project.payloads.EndpointDTO;
import com.test.webhook.project.payloads.EndpointResponse;
import com.test.webhook.project.repositories.EndpointRespository;
import com.test.webhook.project.repositories.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class EndpointServiceImpl implements EndpointService{

    @Autowired
    private EndpointRespository endpointRespository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private UserRepository userRepository;

    @Override
    public EndpointDTO createEndpoint(EndpointDTO endpointDTO, HttpServletRequest request, Long userId) {
        EndpointEntity endpoint = mapper.endpointDataMapper(endpointDTO);
        // Enpoint exist or not check
        Optional<EndpointEntity> endpointFromDB = endpointRespository.findByEndpointName(endpoint.getEndpointName());
        if(endpointFromDB.isPresent()) {
            throw new APIException("Endpoint with name "+endpointDTO.getEndpointName()+" already exist");
        }
        // Get user
        UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        // Associate user with endpoint
        endpoint.setUser(user);
        EndpointEntity savedEndpoint = endpointRespository.save(endpoint);

        EndpointDTO endpointDTOwithCustomUrl = mapper.endpointEntityMapper(savedEndpoint);
        
        String baseURL = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort();
        endpointDTOwithCustomUrl.setCustomEndpointUrl(baseURL +"/api/"+ savedEndpoint.getEndpointName());
       
        return endpointDTOwithCustomUrl;
    }

    @Override
    public EndpointResponse getAllEndpoints(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder, HttpServletRequest request, Long userId) {
        // ---Sorting---
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending(); 
        // ----Pagenation formula----
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<EndpointEntity> endpointPage = endpointRespository.findByUserId(userId, pageDetails);

        List<EndpointEntity> endpoints = endpointPage.getContent();
        if(endpoints.isEmpty()) {
            throw new APIException("No endpoints created till now");
        }
        List<EndpointDTO> endpointDTOs = endpoints.stream()
            .map(ep -> mapper.endpointEntityMapper(ep))
            .toList();
        String baseURL = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort();

        endpointDTOs.forEach(endpointdto -> endpointdto.setCustomEndpointUrl(baseURL + "/api/" + endpointdto.getEndpointName()));


        EndpointResponse endpointResponse = new EndpointResponse();
        endpointResponse.setContent(endpointDTOs);
        endpointResponse.setPageNumber(endpointPage.getNumber());
        endpointResponse.setPageSize(endpointPage.getSize());
        endpointResponse.setTotalElements(endpointPage.getTotalElements());
        endpointResponse.setTotalPages(endpointPage.getTotalPages());
        endpointResponse.setLastPage(endpointPage.isLast());
        return endpointResponse;
    }

    @Override
    public EndpointDTO searchEndpointById(Long endpointId, HttpServletRequest request, Long userId) {
        UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
        
        EndpointEntity endpointFromDB = user.getEndpoints().stream()
                        .filter(e -> e.getEndpointId().equals(endpointId))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointId", endpointId));
        
        EndpointDTO endpointDTOFromDB = mapper.endpointEntityMapper(endpointFromDB);
        String baseURL = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort();

        endpointDTOFromDB.setCustomEndpointUrl(baseURL +"/api/"+ endpointDTOFromDB.getEndpointName());
        
        return endpointDTOFromDB;
    }

    @Override
    public EndpointDTO searchEndpointByName(String endpointName, HttpServletRequest request, Long userId) {
        UserEntity user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));

        EndpointEntity endpointFromDB = user.getEndpoints().stream()
                        .filter(e -> e.getEndpointName().equals(endpointName))
                        .findFirst()
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointName", endpointName));

        EndpointDTO endpointDTOFromDB = mapper.endpointEntityMapper(endpointFromDB);
        String baseURL = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort();

        endpointDTOFromDB.setCustomEndpointUrl(baseURL +"/api/"+ endpointDTOFromDB.getEndpointName());
        
        return endpointDTOFromDB;
    }

    @Override
    public EndpointDTO deleteEndpoint(Long endpointId, HttpServletRequest request) {
        EndpointEntity existingEndpoint = endpointRespository.findById(endpointId)
                        .orElseThrow(() -> new ResourceNotFoundException("Endpoint", "endpointId", endpointId));

        endpointRespository.delete(existingEndpoint);

        EndpointDTO existingEndpointDTO = mapper.endpointEntityMapper(existingEndpoint);
        String baseURL = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort();

        existingEndpointDTO.setCustomEndpointUrl(baseURL +"/api/"+ existingEndpointDTO.getEndpointName());

        return existingEndpointDTO;
    }


    

}
