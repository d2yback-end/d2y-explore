package com.d2y.d2yapiofficial.dto.privilege;

import javax.persistence.Id;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("List Privilege")
public class ListDetailPrivilegeDTO {
  @Id
  private Long rolePrivilegeId;
  private Long privilegeId;
  private String privilegeName;
}
