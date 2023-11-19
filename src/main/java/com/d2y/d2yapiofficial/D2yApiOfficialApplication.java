package com.d2y.d2yapiofficial;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = { "lib.i18n", "com.d2y" })
@EnableAsync
@EnableScheduling
@EnableKafka
@EnableCaching
public class D2yApiOfficialApplication {

  public static void main(String[] args) {
    SpringApplication.run(D2yApiOfficialApplication.class, args);
  }

}
