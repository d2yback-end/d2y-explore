package com.d2y.d2yapiofficial.services;

import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Service
public class TimestampService {
  public Timestamp getUtcTimestamp() {
    ZoneId zoneId = ZoneId.of("UTC+0");
    ZonedDateTime localTime = ZonedDateTime.now(zoneId);
    ZonedDateTime utcTime = localTime.withZoneSameInstant(ZoneOffset.UTC);
    return Timestamp.from(utcTime.toInstant());
  }
}
