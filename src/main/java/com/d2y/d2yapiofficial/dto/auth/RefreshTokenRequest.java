package com.d2y.d2yapiofficial.dto.auth;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {
  @NotBlank
  private String refreshToken;

  @Email(message = "Invalid email address")
  private String email;
}
