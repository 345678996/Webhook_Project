package com.test.webhook.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.test.webhook.project.payloads.UserData;
import com.test.webhook.project.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api", produces = { MediaType.APPLICATION_JSON_VALUE })
@Validated
public class UserController {

	@Autowired
	UserService userService;

	@PostMapping("/users")
	public ResponseEntity<UserData> createUser(@Valid @RequestBody UserData userData) {
    	userData = userService.createUser(userData);
    	return new ResponseEntity<>(userData, HttpStatus.CREATED);
	}

	@GetMapping("/users/{email}")
	public ResponseEntity<UserData> getUserByEmail(@PathVariable(required = true) String email) {
		UserData userData = userService.getUserByEmail(email);
		return new ResponseEntity<>(userData, HttpStatus.OK);
	}

}
