package com.d2y.d2yapiofficial.dto.role;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("Role")
public class RoleDTO {
  private Long roleId;
  private String roleName;
}
