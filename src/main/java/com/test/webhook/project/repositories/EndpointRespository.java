package com.test.webhook.project.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.webhook.project.model.EndpointEntity;

@Repository
public interface EndpointRespository extends JpaRepository<EndpointEntity, Long>{

    Optional<EndpointEntity> findByEndpointName(String endpointName);

    Page<EndpointEntity> findByUserId(Long userId, Pageable pageDetails);

}
