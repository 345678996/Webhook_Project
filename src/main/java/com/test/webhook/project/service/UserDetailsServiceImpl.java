package com.test.webhook.project.service;

import java.util.ArrayList;

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

	public UserDetails loadUserByUsername(String username) {
    	UserEntity userEntityDB = userRepository.findByEmail(username)
        			.orElseThrow(() -> new ResourceNotFoundException("User", "Email", username));

    	return new CustomUserDetails(
        	userEntityDB.getId(),
        	userEntityDB.getEmail(),
        	userEntityDB.getPassword(),
        	new ArrayList<>()
    );
	}
}
