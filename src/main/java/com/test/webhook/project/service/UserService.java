package com.test.webhook.project.service;

import com.test.webhook.project.payloads.UserData;

public interface UserService {
	UserData createUser(UserData userData);

	public UserData getUserByEmail(String email);
}
