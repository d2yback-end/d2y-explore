package com.d2y.d2yapiofficial.dto.privilege;

import java.sql.Timestamp;
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
@JsonApiTypeForClass("Add List Role Privilege")
public class ResponseAddRolePrivilegeDTO {
  @Id
  private Long rolePrivilegeId;
  private Long roleId;
  private String roleName;
  private List<ListDetailPrivilegeDTO> listPrivilege;
  private boolean active;
  private Long createdBy;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private Long updatedBy;
}
