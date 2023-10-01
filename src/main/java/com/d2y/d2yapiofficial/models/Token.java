package com.d2y.d2yapiofficial.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

import java.sql.Timestamp;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Token {
  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "token_id")
  private Long tokenId;

  @Column(name = "token")
  private String token;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "`user_id`", referencedColumnName = "user_id")
  private User user;

  @Column(name = "expiry_date")
  private Timestamp expiryDate;

  @Column(name = "is_expired")
  private boolean expired;

  @Column(name = "created_on")
  private Timestamp createdOn;
}
