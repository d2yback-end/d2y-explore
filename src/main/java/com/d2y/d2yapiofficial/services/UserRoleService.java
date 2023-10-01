package com.d2y.d2yapiofficial.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.ws.rs.ForbiddenException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.dto.role.AddUserRoleDTO;
import com.d2y.d2yapiofficial.dto.role.DetailRoleDTO;
import com.d2y.d2yapiofficial.dto.role.DetailUserRoleDTO;
import com.d2y.d2yapiofficial.dto.role.ResponseAddUserRoleDTO;
import com.d2y.d2yapiofficial.dto.role.ResponseUpdateUserRoleDTO;
import com.d2y.d2yapiofficial.dto.role.UpdateUserRoleDTO;
import com.d2y.d2yapiofficial.models.CategoryCode;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.repositories.UserRepository;
import com.d2y.d2yapiofficial.repositories.UserRoleRepository;
import com.d2y.d2yapiofficial.security.JwtProvider;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRoleService {

  private final UserRepository userRepository;
  private final GetService getService;
  private final UserRoleRepository userRoleRepository;
  private final TimestampService timestampService;
  private final Validator validator;
  private final JwtProvider jwtProvider;

  public Page<DetailUserRoleDTO> convertToRolePrivilegeDTO(Pageable pageable, String search) {
    Page<Map<String, Object>> userRolePage = userRoleRepository.findAllUserRoleId(pageable, search.toLowerCase());

    return userRolePage.map(userRoleMap -> {
      String test = Objects.toString(userRoleMap.get("user_id"));
      Integer userId = Integer.parseInt(test);
      return convertToDetailUserRoleDTO(userId);
    });
  }

  private DetailUserRoleDTO convertToDetailUserRoleDTO(Integer userId) {
    User users = userRepository.findById((long) userId)
        .orElseThrow(() -> new EntityNotFoundException("Users Not Found!"));
    List<UserRole> listUserRoles = userRoleRepository.findByUserIdList(users);
    List<DetailRoleDTO> usersDTOs = listUserRoles.stream().map(this::convertUserRoleDTO).collect(Collectors.toList());
    return DetailUserRoleDTO.builder()
        .userRoleId(users.getUserId())
        .userId(users.getUserId())
        .username(users.getUsername())
        .listRole(usersDTOs)
        .build();
  }

  @Transactional
  public UserRole addUserRole(AddUserRoleDTO userRoleDto) throws Exception {
    try {
      Set<ConstraintViolation<AddUserRoleDTO>> violations = validator.validate(userRoleDto);
      if (!violations.isEmpty()) {
        List<String> errors = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
        throw new ValidationException(errors.get(0));
      }
      User isUserExist = getService.getUser(userRoleDto.getUserId(), ConstantMessage.USER_NOT_FOUND);
      User createdBy = getService.getUser(userRoleDto.getCreatedBy(), ConstantMessage.USER_NOT_FOUND);

      List<Boolean> isPermission = checkPermission(createdBy.getUserId());
      List<UserRole> userExist = userRoleRepository.findByUserIdAndActive(isUserExist);
      UserRole userRole = new UserRole();

      if (isPermission.contains(true)) {
        if (userExist.isEmpty()) {
          for (Long roleId : userRoleDto.getRoleId()) {
            CategoryCode isRoleExist = getService.getCategoryCode(roleId, ConstantMessage.ROLE_NOT_FOUND);
            Optional<UserRole> isUserRoleExist = userRoleRepository.findByUserRoleIdAndUserId(isRoleExist, isUserExist);
            if (!isUserRoleExist.isPresent()) {
              userRole = UserRole.builder()
                  .userId(isUserExist)
                  .roleId(isRoleExist)
                  .active(true)
                  .updatedOn(timestampService.getUtcTimestamp())
                  .updatedBy(userRoleDto.getCreatedBy())
                  .createdBy(userRoleDto.getCreatedBy())
                  .createdOn(timestampService.getUtcTimestamp())
                  .build();
              userRoleRepository.save(userRole);
            } else {
              isUserRoleExist.get().setActive(true);
              userRole = isUserRoleExist.get();
            }
          }
        } else {
          throw new ValidationException("User Role Data Already Exist!");
        }
      } else {
        throw new ForbiddenException("You Don't Have Permission To Add UserRole!");
      }
      return userRole;
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  public ResponseAddUserRoleDTO convertAddUserRoleDTO(UserRole userRole) {
    List<UserRole> userRoles = userRoleRepository.findByIdAndActiveList(userRole.getUserId());
    List<DetailRoleDTO> userRoleDTOs = userRoles.stream().map(this::convertUserRoleDTO).collect(Collectors.toList());
    return ResponseAddUserRoleDTO.builder()
        .userRoleId(userRole.getUserRoleId())
        .detailRole(userRoleDTOs)
        .userId(userRole.getUserId().getUserId())
        .active(userRole.isActive())
        .updatedOn(userRole.getUpdatedOn())
        .updatedBy(userRole.getUpdatedBy())
        .createdBy(userRole.getCreatedBy())
        .createdOn(userRole.getCreatedOn())
        .build();
  }

  public ResponseUpdateUserRoleDTO convertUpdateUserRoleDTO(Long id) {
    User users = getService.getUser(id, ConstantMessage.USER_NOT_FOUND);
    List<UserRole> userRoles = userRoleRepository.findByIdAndActiveList(users);
    List<DetailRoleDTO> userRoleDTOs = userRoles.stream().map(this::convertUserRoleDTO).collect(Collectors.toList());
    return ResponseUpdateUserRoleDTO.builder()
        .userId(id)
        .detailRole(userRoleDTOs)
        .build();
  }

  @Transactional
  public List<UserRole> updateUserRole(Long id, UpdateUserRoleDTO userRoleDTO) throws Exception {
    try {
      Set<ConstraintViolation<UpdateUserRoleDTO>> violations = validator.validate(userRoleDTO);
      if (!violations.isEmpty()) {
        List<String> errors = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
        throw new ValidationException(errors.get(0));
      }
      User users = getService.getUser(id, ConstantMessage.USER_NOT_FOUND);
      List<UserRole> userRole = userRoleRepository.findByIdAndActiveList(users);
      User updatedBy = getService.getUser(userRoleDTO.getUpdatedBy(), ConstantMessage.USER_NOT_FOUND);
      List<Boolean> checkPermission = checkPermission(updatedBy.getUserId());
      if (checkPermission.contains(true)) {
        for (UserRole uRole : userRole) {
          uRole.setActive(false);
        }
        userRoleRepository.saveAll(userRole);
        checkUserRole(id, userRole, userRoleDTO);
      } else {
        throw new ForbiddenException("You Don't Have Permission To Update UserRole!");
      }
      return userRole;
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  @Transactional
  public void checkUserRole(Long id, List<UserRole> userRoles, UpdateUserRoleDTO userRoleDTO) {
    try {
      for (int i = 0; i < userRoleDTO.getRoleId().size(); i++) {
        User users = getService.getUser(id, ConstantMessage.USER_NOT_FOUND);
        CategoryCode userRoleId = getService.getCategoryCode(userRoleDTO.getRoleId().get(i),
            ConstantMessage.ROLE_NOT_FOUND);
        Optional<UserRole> uOptional = userRoleRepository.findByUserRoleIdAndUserId(userRoleId, users);
        if (uOptional.isPresent()) {
          uOptional.get().setActive(true);
          uOptional.get().setUpdatedOn(new Timestamp(System.currentTimeMillis()));
          uOptional.get().setUpdatedBy(userRoleDTO.getUpdatedBy());
          userRoleRepository.save(uOptional.get());
        } else {
          UserRole userRole = UserRole.builder()
              .userId(users)
              .roleId(userRoleId)
              .active(true)
              .createdBy(userRoleDTO.getUpdatedBy())
              .createdOn(timestampService.getUtcTimestamp())
              .updatedOn(timestampService.getUtcTimestamp())
              .updatedBy(userRoleDTO.getUpdatedBy())
              .build();
          userRoleRepository.save(userRole);
        }
      }
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  @Transactional
  public void deleteRoleUser(String token, Long id) {
    try {
      String email = jwtProvider.getEmailFromToken(token);
      User updatedBy = getService.getUserByEmail(email, ConstantMessage.USER_NOT_FOUND);
      User users = getService.getUser(id, ConstantMessage.USER_NOT_FOUND);

      List<UserRole> userRoles = userRoleRepository.findByIdAndActiveList(users);
      List<Boolean> checkPermission = checkPermission(updatedBy.getUserId());

      if (checkPermission.contains(true)) {
        for (UserRole uRole : userRoles) {
          uRole.setActive(false);
          uRole.setUpdatedBy(updatedBy.getUserId());
          uRole.setUpdatedOn(timestampService.getUtcTimestamp());
        }
        userRoleRepository.saveAll(userRoles);
      } else {
        throw new ForbiddenException("You Don't Have Permission To Delete UserRole!");
      }
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  public DetailUserRoleDTO getDetailUserRole(Long id) {
    User users = getService.getUser(id, ConstantMessage.USER_NOT_FOUND);
    List<UserRole> userRole = userRoleRepository.findByIdAndActiveList(users);
    List<DetailRoleDTO> userRoleDTOs = userRole.stream().map(this::convertUserRoleDTO).collect(Collectors.toList());
    return DetailUserRoleDTO.builder()
        .userRoleId(id)
        .userId(users.getUserId())
        .username(users.getUsername())
        .listRole(userRoleDTOs)
        .build();
  }

  private DetailRoleDTO convertUserRoleDTO(UserRole userRole) {
    return DetailRoleDTO.builder()
        .userRoleId(userRole.getUserRoleId())
        .roleId(userRole.getRoleId().getCategoryCodeId())
        .role(userRole.getRoleId().getCodeName())
        .active(userRole.isActive())
        .build();
  }

  private List<Boolean> checkPermission(Long userId) {
    User user = getUser(userId, ConstantMessage.USER_NOT_FOUND);
    List<UserRole> listUserRole = userRoleRepository.findByIdAndActiveList(user);
    List<Boolean> isPermission = new ArrayList<>();
    for (UserRole uRole : listUserRole) {
      isPermission.add(userRoleRepository.isPermissionForUserRoleManipulation(uRole.getRoleId().getCategoryCodeId()));
    }
    return isPermission;
  }

  public User getUser(Long id, String message) {
    return userRepository.findByIdAndActive(id).orElseThrow(() -> new EntityNotFoundException(message));
  }

}
