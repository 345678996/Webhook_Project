package com.test.webhook.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.test.webhook.project.exceptions.ResourceNotFoundException;
import com.test.webhook.project.model.UserEntity;
import com.test.webhook.project.repositories.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) {

		UserEntity userEntityDB = userRepository.findByEmail(username)
				.orElseThrow(() -> new ResourceNotFoundException("User", "Email", username));
		
		return org.springframework.security.core.userdetails.User
	            .withUsername(userEntityDB.getEmail())
	            .password(userEntityDB.getPassword())
	            .roles("USER")
	            .build();

	}
}
