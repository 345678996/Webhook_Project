package com.test.webhook.project.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.test.webhook.project.model.EndpointEntity;
import com.test.webhook.project.model.IncomingRequestEntity;

@Repository
public interface IncomingRequestRespository extends JpaRepository<IncomingRequestEntity, Long>{

    Page<IncomingRequestEntity> findByEndpointOrderByReceivedAtAsc(EndpointEntity endpoint, Pageable pageDetails);

    Optional<IncomingRequestEntity> findByRequestIdAndEndpoint(Long requestId, EndpointEntity endpoint);

    List<IncomingRequestEntity> findByEndpointOrderByReceivedAtAsc(EndpointEntity endpoint);

}
