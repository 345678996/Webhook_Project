package com.test.webhook.project.payloads;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserData {
	@NotEmpty(message = "Email can not be a empty")
	@Email(message = "Email should be a valid value")
	private String email;
	private String alias;
	@NotEmpty(message = "Password can not be a empty")
	private String password;
	private String guid;
}
