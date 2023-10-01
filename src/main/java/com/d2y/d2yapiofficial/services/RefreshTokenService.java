package com.d2y.d2yapiofficial.services;

import java.sql.Timestamp;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.d2y.d2yapiofficial.models.RefreshToken;
import com.d2y.d2yapiofficial.repositories.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional
public class RefreshTokenService {

  private final RefreshTokenRepository refreshTokenRepository;

  public RefreshToken generateRefreshToken() {
    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setToken(UUID.randomUUID().toString());
    refreshToken.setCreatedOn(new Timestamp(System.currentTimeMillis()));

    return refreshTokenRepository.save(refreshToken);
  }

  void validateRefreshToken(String token) {
    refreshTokenRepository.findByToken(token)
        .orElseThrow(() -> new ValidationException("Invalid Refresh Token"));
  }

  public void deleteRefreshToken(String token) {
    refreshTokenRepository.deleteByToken(token);
  }
}