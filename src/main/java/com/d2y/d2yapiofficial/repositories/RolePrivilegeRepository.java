package com.d2y.d2yapiofficial.repositories;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.d2y.d2yapiofficial.models.CategoryCode;
import com.d2y.d2yapiofficial.models.RolePrivilege;

public interface RolePrivilegeRepository extends JpaRepository<RolePrivilege, Long> {

  Optional<RolePrivilege> findByRolePrivilegeId(Long rolePrivilegeId);

  @Query("SELECT rp FROM RolePrivilege rp WHERE rp.roleId = :id ")
  Optional<RolePrivilege> findByRoleId(CategoryCode id);

  @Query("SELECT rp FROM RolePrivilege rp WHERE rp.roleId = :id AND rp.active = true")
  List<RolePrivilege> findByIdList(CategoryCode id);

  @Query(nativeQuery = true, value = "SELECT rp.role_id, cc.code_name AS roleName, COUNT(rp.role_id) AS privilege  FROM role_privilege rp "
      +
      "LEFT JOIN category_code cc ON rp.role_id = cc.category_code_id " +
      "LEFT JOIN category_code pi ON rp.privilege_id = pi.category_code_id " +
      "WHERE (rp.active = true AND (:search IS NULL OR LOWER (cc.code_name) LIKE %:search% " +
      "OR LOWER (pi.code_name) LIKE %:search%)) " +
      "GROUP BY rp.role_id, cc.code_name")
  Page<Map<String, Object>> findAllRoleId(Pageable pageable, @Param("search") String search);

  @Query(nativeQuery = true, value = "SELECT COUNT(DISTINCT ur.role_id) FROM role_privilege ur WHERE (ur.active = 'true')")
  Long findRolePrivilegeActive();

  @Query("SELECT rp FROM RolePrivilege rp WHERE rp.roleId = :id AND rp.active = true")
  List<RolePrivilege> findIdActive(CategoryCode id);

  @Query("SELECT rp FROM RolePrivilege rp WHERE rp.roleId = :roleId AND rp.privilegeId = :privilegeId")
  Optional<RolePrivilege> findByUserRoleAndPrivilege(@Param("roleId") CategoryCode roleId,
      @Param("privilegeId") CategoryCode privilegeId);

  @Query("SELECT rp FROM RolePrivilege rp WHERE rp.active = true AND rp.roleId = :id")
  List<RolePrivilege> findByUserIdAndActive(CategoryCode id);

  @Query("SELECT rp FROM RolePrivilege rp WHERE rp.roleId = :id AND rp.active = true")
  List<RolePrivilege> getListRolePrivilege(CategoryCode id);
}
