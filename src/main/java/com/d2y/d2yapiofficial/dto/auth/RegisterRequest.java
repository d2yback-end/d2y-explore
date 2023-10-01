package com.d2y.d2yapiofficial.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
  @NotBlank(message = "Username cannot be empty")
  @Size(min = 1, max = 100, message = "Username must be between 1 and 100 characters")
  private String username;

  @NotBlank(message = "Password cannot be empty")
  @Size(min = 8, message = "Password must be at least 8 characters long")
  private String password;

  @NotBlank(message = "Email cannot be empty")
  @Email(message = "Invalid email format")
  private String email;
}
