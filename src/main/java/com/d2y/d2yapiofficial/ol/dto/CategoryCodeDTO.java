package com.d2y.d2yapiofficial.ol.dto;

import com.d2y.d2yapiofficial.models.CategoryCode;
import com.toedter.spring.hateoas.jsonapi.JsonApiTypeForClass;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonApiTypeForClass("OptionList")
public class CategoryCodeDTO {

  private Long id;
  private String name;

  public CategoryCodeDTO(CategoryCode cc) {
    this.id = cc.getCategoryCodeId();
    this.name = cc.getCodeName();
  }

}
