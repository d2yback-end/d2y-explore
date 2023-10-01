package com.d2y.d2yapiofficial.repositories;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d2y.d2yapiofficial.models.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

  Optional<Token> findByToken(String token);

  List<Token> findByExpiryDateBeforeAndExpiredIsFalse(Timestamp expiryDate);

  List<Token> findByExpiredIsTrue();

}
