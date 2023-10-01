package com.d2y.d2yapiofficial.dto.role;

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
@JsonApiTypeForClass("Add User Role")
public class ResponseAddUserRoleDTO {
  @Id
  private Long userRoleId;
  private Long userId;
  private List<DetailRoleDTO> detailRole;
  private boolean active;
  private Long createdBy;
  private Timestamp createdOn;
  private Long updatedBy;
  private Timestamp updatedOn;
}
