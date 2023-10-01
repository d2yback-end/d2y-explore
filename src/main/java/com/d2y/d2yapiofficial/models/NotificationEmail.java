package com.d2y.d2yapiofficial.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEmail {
  private String subject;
  private String recipient;
  private String body;
  private String username;
  private String verificationUrl;
}