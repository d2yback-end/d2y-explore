package com.d2y.d2yapiofficial.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

import com.d2y.d2yapiofficial.dto.privilege.PrivilegeDTO;
import com.d2y.d2yapiofficial.dto.role.RoleDTO;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("Authentication")
public class AuthResponse {
  private Long userId;
  private String email;
  private String username;
  private List<RoleDTO> listRole;
  private List<PrivilegeDTO> listPrivilege;
  private String accessToken;
  private String refreshToken;
}
