package com.d2y.d2yapiofficial.models;

import java.sql.Timestamp;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long userId;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "email")
  private String email;

  @Column(name = "date_of_birth", nullable = true)
  private LocalDate dateOfBirth;

  @Column(name = "phone_number", nullable = true)
  private String phoneNumber;

  @Column(name = "registration_date")
  private Timestamp registrationDate;

  @Column(name = "last_login")
  private Timestamp lastLogin;

  @Column(name = "photo_profile", nullable = true)
  private String photoProfile;

  @Column(name = "created_on")
  private Timestamp createdOn;

  @Column(name = "updated_on")
  private Timestamp updatedOn;

  @Column(name = "is_active")
  private boolean active;

  @Column(name = "is_enable")
  private boolean enabled;

  @Column(name = "bio", nullable = true)
  private String bio;

  @Column(name = "ig_account", nullable = true)
  private String igAccount;

  @Column(name = "twitter_account", nullable = true)
  private String twitterAccount;

  @Column(name = "fb_account", nullable = true)
  private String fbAccount;

  @Column(name = "yt_account", nullable = true)
  private String ytAccount;

  @Column(name = "address", nullable = true)
  private String address;

  @Column(name = "website", nullable = true)
  private String website;

  @Column(name = "gender", nullable = true)
  private String gender;

}
