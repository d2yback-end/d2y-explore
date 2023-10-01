package com.d2y.d2yapiofficial.scheduler;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.models.Token;
import com.d2y.d2yapiofficial.repositories.TokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TokenSchedule {

  private TokenRepository tokenRepository;

  @Scheduled(cron = "0 0 0 * * ?") // Setiap hari pada pukul 12 malam
  public void checkAndSetTokenExpired() {
    LocalDateTime now = LocalDateTime.now();
    Timestamp currentTimestamp = Timestamp.valueOf(now);

    List<Token> tokens = tokenRepository.findByExpiryDateBeforeAndExpiredIsFalse(currentTimestamp);

    for (Token token : tokens) {
      LocalDateTime expiryDateTime = token.getExpiryDate().toLocalDateTime();
      if (now.isAfter(expiryDateTime.plusHours(24))) {
        token.setExpired(true);
        tokenRepository.save(token);
      }
    }
  }

  @Scheduled(cron = "0 0 6 * * SUN") // Setiap hari Minggu jam 6 pagi
  public void deleteExpiredTokens() {
    List<Token> expiredTokens = tokenRepository.findByExpiredIsTrue();

    for (Token token : expiredTokens) {
      tokenRepository.delete(token);
    }
  }

}
