package com.d2y.d2yapiofficial.dto.role;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;

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
public class UpdateUserRoleDTO {
  @Id
  private Long userRoleId;
  private Long userId;
  @NotEmpty(message = "Please input Role")
  private List<Long> roleId;
  private boolean active;
  private Long createdBy;
  private Timestamp createdOn;
  private Long updatedBy;
  private Timestamp updatedOn;
}
