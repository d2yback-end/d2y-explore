package com.d2y.d2yapiofficial.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestParam;

import com.d2y.d2yapiofficial.models.CategoryCode;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.models.User;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
  @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :id AND ur.active = true AND ur.roleId IS NOT NULL")
  List<UserRole> findByIdAndActiveList(User id);

  @Query(nativeQuery = true, value = "SELECT ur.user_id, u.username AS name, COUNT(ur.user_id) AS role FROM user_role ur "
      + "LEFT JOIN users u ON ur.user_id = u.user_id "
      + "LEFT JOIN category_code cc ON ur.role_id = cc.category_code_id "
      + "WHERE (ur.is_active = true AND (:search IS NULL OR LOWER(u.username) LIKE %:search% OR LOWER(cc.code_name) LIKE %:search% )) "
      + "GROUP BY ur.user_id, u.username")
  Page<Map<String, Object>> findAllUserRoleId(Pageable pageable, @Param("search") String search);

  @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT ur.user_id) FROM user_role ur WHERE (ur.active = 'true')")
  Long findUserRoleActive();

  @Query("SELECT ur FROM UserRole ur WHERE ur.userId = :id AND ur.active = true")
  List<UserRole> findByUserIdList(User id);

  @Query("SELECT ur FROM UserRole ur WHERE ur.roleId = :id")
  List<UserRole> findByCategoryIdList(CategoryCode id);

  @Query("SELECT ur FROM UserRole ur WHERE ur.roleId = :id AND ur.userId = :uid")
  Optional<UserRole> findByUserRoleIdAndUserId(CategoryCode id, User uid);

  @Query("SELECT ur FROM UserRole ur WHERE (ur.active = 'true') AND ur.userId = :uid")
  List<UserRole> findByUserIdAndActive(@Param("uid") User uid);

  @Query("SELECT ur FROM UserRole ur WHERE ur.active = true AND ur.roleId = :id")
  List<UserRole> findRoleIdActive(@RequestParam("id") CategoryCode id);

  @Query("SELECT ur FROM UserRole ur WHERE ur.userId =:searchId")
  UserRole findUserRoleByUserId(@Param("searchId") User searchId);

  @Query("SELECT CASE WHEN COUNT(cc) > 0 THEN TRUE ELSE FALSE END FROM CategoryCode cc " +
      "WHERE cc.categoryCodeId = :id AND (cc.codeName IN ('Administrator'))")
  boolean isPermissionForUserRoleManipulation(Long id);
}
