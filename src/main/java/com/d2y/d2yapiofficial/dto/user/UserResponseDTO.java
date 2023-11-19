package com.d2y.d2yapiofficial.dto.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import javax.persistence.Id;

import com.d2y.d2yapiofficial.models.User;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonApiTypeForClass("Users")
public class UserResponseDTO implements Serializable {
  @Id
  private Long userId;
  private String username;
  private String email;
  private String photoProfile;
  private String phoneNumber;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private Timestamp registrationDate;
  private Timestamp lastLogin;
  private boolean active;
  private boolean enabled;
  private String bio;
  private String igAccount;
  private String twitterAccount;
  private String fbAccount;
  private String ytAccount;
  private String address;
  private String website;
  private String gender;
  private String dateOfBirth;

  public UserResponseDTO(User users) {
    this.userId = users.getUserId();
    this.username = users.getUsername();
    this.email = users.getEmail();
    this.phoneNumber = users.getPhoneNumber();
    this.photoProfile = users.getPhotoProfile();
    this.createdOn = users.getCreatedOn();
    this.updatedOn = users.getUpdatedOn();
    this.registrationDate = users.getRegistrationDate();
    this.lastLogin = users.getLastLogin();
    this.active = users.isActive();
    this.enabled = users.isEnabled();
    this.bio = users.getBio();
    this.igAccount = users.getIgAccount();
    this.twitterAccount = users.getTwitterAccount();
    this.fbAccount = users.getFbAccount();
    this.ytAccount = users.getYtAccount();
    this.address = users.getAddress();
    this.website = users.getWebsite();
    this.gender = users.getGender();

    if (users.getDateOfBirth() != null) {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy");
      this.dateOfBirth = users.getDateOfBirth().format(formatter);
    } else {
      this.dateOfBirth = "";
    }
  }

}
