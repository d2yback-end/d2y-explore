package com.d2y.d2yapiofficial.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.d2y.d2yapiofficial.dto.auth.AuthResponse;
import com.d2y.d2yapiofficial.dto.auth.LoginRequest;
import com.d2y.d2yapiofficial.dto.auth.RefreshTokenRequest;
import com.d2y.d2yapiofficial.dto.auth.RegisterRequest;
import com.d2y.d2yapiofficial.services.AuthService;
import com.d2y.d2yapiofficial.services.RefreshTokenService;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

  private final AuthService authService;
  private final RefreshTokenService refreshTokenService;

  @PostMapping("/register")
  public ResponseEntity<Object> register(@RequestBody RegisterRequest registerRequest) {
    authService.registerUser(registerRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(JsonApiModelBuilder.jsonApiModel()
            .meta(ConstantMessage.MESSAGE, ConstantMessage.REGISTER_SUCCESS)
            .build());

  }

  @GetMapping("accountVerification/{token}")
  public ResponseEntity<Object> verifyAccount(@PathVariable String token) {
    boolean isNotExpired = authService.verifyAccount(token);

    if (isNotExpired) {
      return ResponseEntity.status(HttpStatus.OK)
          .body("Congratulations, Your Account Activation Was Successful");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("The token has expired. Please Login again!");
    }
  }

  @PostMapping("/login")
  public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
    AuthResponse authResponse = authService.login(loginRequest);
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/refresh/token")
  public ResponseEntity<Object> refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    AuthResponse authResponse = authService.refreshToken(refreshTokenRequest);
    return ResponseEntity.ok(authResponse);
  }

  @PostMapping("/logout")
  public ResponseEntity<Object> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
    return ResponseEntity.status(HttpStatus.OK)
        .body(JsonApiModelBuilder.jsonApiModel()
            .meta(ConstantMessage.MESSAGE, ConstantMessage.REFRESH_TOKEN_DELETED)
            .build());
  }
}