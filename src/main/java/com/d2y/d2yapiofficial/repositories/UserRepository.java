package com.d2y.d2yapiofficial.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.d2y.d2yapiofficial.dto.user.UserResponseDTO;
import com.d2y.d2yapiofficial.models.User;

public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  @Query("SELECT u FROM User u WHERE u.userId = :id AND u.active = true")
  Optional<User> findByIdAndActive(Long id);

  @Query("SELECT new com.d2y.d2yapiofficial.dto.user.UserResponseDTO(u) FROM User u " +
      "WHERE (:search IS NULL OR LOWER(u.username) LIKE %:search% " +
      "OR LOWER(u.email) LIKE %:search%) AND u.active = true")
  Page<UserResponseDTO> getListUsers(@Param("search") String search, Pageable pageable);

  @Query(value = "SELECT * FROM User u WHERE u.email = :email AND u.is_active = true", nativeQuery = true)
  Optional<User> findByEmailAndActive(String email);

}