package com.d2y.d2yapiofficial.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.d2y.d2yapiofficial.models.CategoryCode;
import com.d2y.d2yapiofficial.ol.dto.CategoryCodeDTO;

import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryCodeRepository extends JpaRepository<CategoryCode, Long> {

  @Query("SELECT new com.d2y.d2yapiofficial.ol.dto.CategoryCodeDTO(cc) FROM CategoryCode cc " +
      "WHERE cc.categoryName LIKE :categoryName " +
      "AND (:search IS NULL OR LOWER(cc.codeName) LIKE %:search%)")
  List<CategoryCodeDTO> findAllByCategoryNameAndSearch(String categoryName, String search);

  @Query("SELECT cc FROM CategoryCode cc " +
      "WHERE cc.categoryName =:categoryName " +
      "AND cc.categoryCodeId =:id")
  Optional<CategoryCode> findRoleProjectId(String categoryName, Long id);

  Optional<CategoryCode> findByCategoryCodeId(Long categoryCodeId);

  @Query("SELECT cc FROM CategoryCode cc WHERE cc.categoryCodeId = :id")
  CategoryCode findId(Long id);

  @Query("SELECT cc.categoryCodeId FROM CategoryCode cc " +
      "WHERE cc.codeName LIKE %:codeName%")
  Optional<Long> findIdByCodeName(String codeName);

  @Query("SELECT cc FROM CategoryCode cc WHERE cc.categoryCodeId=:categoryCodeId ")
  CategoryCode findByCategoryCodeIdDirect(Long categoryCodeId);

}
