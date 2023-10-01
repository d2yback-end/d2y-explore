package com.d2y.d2yapiofficial.scheduler;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.repositories.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RefreshTokenSchedule {

  private final RefreshTokenRepository refreshTokenRepository;

  @Scheduled(cron = "0 0 0 * * 0") // Setiap hari Minggu pukul 00:00 (midnight)
  public void deleteExpiredRefreshTokens() {
    LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
    Timestamp timestampOneWeekAgo = Timestamp.valueOf(oneWeekAgo);

    refreshTokenRepository.deleteByCreatedOnBefore(timestampOneWeekAgo);
  }

}
