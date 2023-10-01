package com.d2y.d2yapiofficial.dto.role;

import java.util.List;

import javax.persistence.Id;

import com.d2y.d2yapiofficial.models.UserRole;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("Detail User Role")
public class DetailUserRoleDTO {
  @Id
  private Long userRoleId;
  private Long userId;
  private String username;
  private List<DetailRoleDTO> listRole;

  public DetailUserRoleDTO(UserRole userRole) {
    this.userRoleId = userRole.getUserRoleId();
    this.userId = userRole.getUserId().getUserId();
    this.userRoleId = userRole.getUserRoleId();
    this.username = userRole.getUserId().getUsername();
  }
}
