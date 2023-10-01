package com.d2y.d2yapiofficial.dto.privilege;

import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonApiTypeForClass("Privilege")
public class PrivilegeDTO {
  private Long privilegeId;
  private String privilegeName;
}
