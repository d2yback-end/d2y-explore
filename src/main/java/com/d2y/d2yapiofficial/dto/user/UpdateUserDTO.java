package com.d2y.d2yapiofficial.dto.user;

import java.time.LocalDate;

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
public class UpdateUserDTO {
  private String username;
  private LocalDate dateOfBirth;
  private String phoneNumber;
  private String photoProfile;
  private String bio;
  private String igAccount;
  private String twitterAccount;
  private String fbAccount;
  private String ytAccount;
  private String address;
  private String website;
  private String gender;
}
