package com.test.webhook.project.configurations;

import java.util.UUID;

import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.test.webhook.project.model.EndpointEntity;
import com.test.webhook.project.model.UserEntity;
import com.test.webhook.project.payloads.EndpointDTO;
import com.test.webhook.project.payloads.UserData;

@Component
public class Mapper {

    @Autowired
	private PasswordEncoder passwordEncoder;

    // DTO -> Entity [USER]
    public UserEntity userDataMapper(UserData userData) {
		UserEntity userEntity = new UserEntity();
		userEntity.setAlias(userData.getAlias());
		userEntity.setEmail(userData.getEmail());
		userEntity.setGuid(UUID.randomUUID().toString());
		userEntity.setPassword(passwordEncoder.encode(userData.getPassword()));
		userEntity.setIsEmailVerified(Boolean.FALSE);
		return userEntity;
	}

    // Entity -> DTO [USER]
	public UserData userEntityMapper(UserEntity userEntity) {
		UserData userDataDB = new UserData();
		userDataDB.setAlias(userEntity.getAlias());
		userDataDB.setEmail(userEntity.getEmail());
		userDataDB.setGuid(userEntity.getGuid());
		return userDataDB;
	}

	// DTO -> Entity [ENDPOINT]
	public EndpointEntity endpointDataMapper(EndpointDTO endpointDTO) {
		EndpointEntity endpoint = new EndpointEntity();
		endpoint.setEndpointId(endpointDTO.getEndpointId());
		endpoint.setEndpointName(endpointDTO.getEndpointName());
		endpoint.setDescription(endpointDTO.getDescription());
		return endpoint;
	}

	// Entity -> DTO [ENDPOINT]
	public EndpointDTO endpointEntityMapper(EndpointEntity endpoint) {
		EndpointDTO endpointDTO = new EndpointDTO();
		endpointDTO.setEndpointId(endpoint.getEndpointId());
		endpointDTO.setEndpointName(endpoint.getEndpointName());
		endpointDTO.setDescription(endpoint.getDescription());
		return endpointDTO;
	}

}
