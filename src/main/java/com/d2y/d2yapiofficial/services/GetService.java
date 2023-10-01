package com.d2y.d2yapiofficial.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.models.CategoryCode;
import com.d2y.d2yapiofficial.models.RolePrivilege;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.repositories.CategoryCodeRepository;
import com.d2y.d2yapiofficial.repositories.RolePrivilegeRepository;
import com.d2y.d2yapiofficial.repositories.UserRepository;
import com.d2y.d2yapiofficial.repositories.UserRoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class GetService {

  private final CategoryCodeRepository categoryCodeRepository;
  private final UserRepository userRepository;
  private final RolePrivilegeRepository rolePrivilegeRepository;
  private final UserRoleRepository userRoleRepository;
  private final Validator validator;

  public User getUser(Long id, String message) {
    return userRepository.findByIdAndActive(id).orElseThrow(() -> new EntityNotFoundException(message));
  }

  public User getUserByEmail(String email, String message) {
    return userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(message));
  }

  public CategoryCode getCategoryCode(Long id, String message) {
    return categoryCodeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(message));
  }

  public RolePrivilege getRolePrivilege(UserRole role, String message) {
    return rolePrivilegeRepository.findByRoleId(role.getRoleId())
        .orElseThrow(() -> new EntityNotFoundException(message));
  }

  public String getToken(HttpServletRequest request) {
    return request.getHeader("Authorization").substring(6);
  }

  public List<UserRole> getUserRoleList(User users) {
    return userRoleRepository.findByIdAndActiveList(users);
  }

  public void validate(Object dto) {
    Set<ConstraintViolation<Object>> violations = validator.validate(dto);
    if (!violations.isEmpty()) {
      List<String> errors = violations.stream()
          .map(ConstraintViolation::getMessage)
          .collect(Collectors.toList());
      throw new ValidationException(errors.get(0));
    }
  }
}
