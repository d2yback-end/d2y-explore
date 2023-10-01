package com.d2y.d2yapiofficial.ol.dto;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import com.d2y.d2yapiofficial.models.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonApiTypeForClass("OptionList")
public class UserListDTO {
  @Id
  private Long userId;
  private String username;

  public UserListDTO(User users) {
    this.userId = users.getUserId();
    this.username = users.getUsername();
  }
}
