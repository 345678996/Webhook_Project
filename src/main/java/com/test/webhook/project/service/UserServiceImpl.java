package com.test.webhook.project.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.test.webhook.project.configurations.Mapper;
import com.test.webhook.project.exceptions.ResourceNotFoundException;
import com.test.webhook.project.exceptions.UserAlreadyExistsException;
import com.test.webhook.project.model.UserEntity;
import com.test.webhook.project.payloads.UserData;
import com.test.webhook.project.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private Mapper mapper;

	@Override
	public UserData getUserByEmail(String email) {
		UserEntity userEntityDB = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Email", email));

		UserData userDataDB = mapper.userEntityMapper(userEntityDB);
		return userDataDB;
	}

	@Override
	public UserData createUser(UserData userData) {
		userRepository.findByEmail(userData.getEmail())
        .ifPresent(user -> {
            throw new UserAlreadyExistsException("Email already exists: " + userData.getEmail());
        });

		UserEntity userEntity = mapper.userDataMapper(userData);

		UserEntity userEntityDB = userRepository.saveAndFlush(userEntity);
		return mapper.userEntityMapper(userEntityDB);	
	}

}
