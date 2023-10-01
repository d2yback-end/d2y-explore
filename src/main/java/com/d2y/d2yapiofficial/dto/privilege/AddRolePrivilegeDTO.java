package com.d2y.d2yapiofficial.dto.privilege;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;
import lombok.*;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
@JsonApiTypeForClass("Add Role Privilege")
public class AddRolePrivilegeDTO {
  @Id
  private Long rolePrivilegeId;
  @NotNull(message = "Please input Role")
  private Long roleId;
  private String roleName;
  @NotEmpty(message = "Please input Privilege")
  private List<Long> listPrivilege;
  private Long createdBy;
  private Timestamp createdOn;
  private Timestamp updatedOn;
  private Long updatedBy;
}
