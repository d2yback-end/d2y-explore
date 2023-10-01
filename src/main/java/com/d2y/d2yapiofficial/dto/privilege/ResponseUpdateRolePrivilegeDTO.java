package com.d2y.d2yapiofficial.dto.privilege;

import java.util.List;

import javax.persistence.Id;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("Response Update Role Privilege")
public class ResponseUpdateRolePrivilegeDTO {
  @Id
  private Long roleId;
  private String roleName;
  private List<ListDetailPrivilegeDTO> privilegeId;
}
