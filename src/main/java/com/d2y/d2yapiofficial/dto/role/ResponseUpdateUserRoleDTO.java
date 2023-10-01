package com.d2y.d2yapiofficial.dto.role;

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
@JsonApiTypeForClass("Update User Role")
public class ResponseUpdateUserRoleDTO {
  @Id
  private Long userId;
  private List<DetailRoleDTO> detailRole;
}
