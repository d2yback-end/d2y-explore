package com.d2y.d2yapiofficial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.d2y.d2yapiofficial.models.RefreshToken;

import java.sql.Timestamp;
import java.util.Optional;

import javax.transaction.Transactional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

  Optional<RefreshToken> findByToken(String token);

  void deleteByToken(String token);

  @Modifying
  @Transactional
  void deleteByCreatedOnBefore(Timestamp timestamp);

}
