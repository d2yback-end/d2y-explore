package com.d2y.d2yapiofficial.dto.privilege;

import java.util.List;

import javax.persistence.Id;

import com.d2y.d2yapiofficial.models.CategoryCode;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("Detail Role Privilege")
public class DetailRolePrivilegeDTO {
  @Id
  private Long roleId;
  private String roleName;
  private List<ListDetailPrivilegeDTO> listPrivilege;

  public DetailRolePrivilegeDTO(CategoryCode rolePrivilege) {
    this.roleId = rolePrivilege.getCategoryCodeId();
    this.roleName = rolePrivilege.getCodeName();
  }
}
