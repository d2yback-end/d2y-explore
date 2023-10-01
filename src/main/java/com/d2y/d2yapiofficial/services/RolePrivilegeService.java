package com.d2y.d2yapiofficial.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
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

import com.d2y.d2yapiofficial.dto.privilege.AddRolePrivilegeDTO;
import com.d2y.d2yapiofficial.dto.privilege.DetailRolePrivilegeDTO;
import com.d2y.d2yapiofficial.dto.privilege.ListDetailPrivilegeDTO;
import com.d2y.d2yapiofficial.dto.privilege.ResponseAddRolePrivilegeDTO;
import com.d2y.d2yapiofficial.dto.privilege.ResponseUpdateRolePrivilegeDTO;
import com.d2y.d2yapiofficial.dto.privilege.UpdateRolePrivilegeDTO;
import com.d2y.d2yapiofficial.models.CategoryCode;
import com.d2y.d2yapiofficial.models.RolePrivilege;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.repositories.CategoryCodeRepository;
import com.d2y.d2yapiofficial.repositories.RolePrivilegeRepository;
import com.d2y.d2yapiofficial.repositories.UserRoleRepository;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RolePrivilegeService {
  private final GetService getService;
  private final UserRoleRepository userRoleRepository;
  private final RolePrivilegeRepository rolePrivilegeRepository;
  private final CategoryCodeRepository categoryCodeRepository;
  private final TimestampService timestampService;
  private final Validator validator;

  public Long getAllRollePrivilege() {
    return rolePrivilegeRepository.findRolePrivilegeActive();
  }

  @Transactional
  public void deleteRolePrivilege(Long id) {
    try {
      CategoryCode role = categoryCodeRepository.findByCategoryCodeId(id)
          .orElseThrow(() -> new EntityNotFoundException("Role Id Not Found"));
      List<RolePrivilege> privilege = rolePrivilegeRepository.findByIdList(role);
      List<UserRole> cekRole = userRoleRepository.findRoleIdActive(role);
      if (cekRole.isEmpty()) {
        for (RolePrivilege uRole : privilege) {
          uRole.setActive(false);
          uRole.setUpdateOn(timestampService.getUtcTimestamp());
        }
        rolePrivilegeRepository.saveAll(privilege);
      } else {
        throw new EntityExistsException("The Role cannot be deleted because there are still User Role.");
      }
    } catch (Exception ex) {
      log.info(ex.getMessage());
      throw ex;
    }
  }

  public Page<DetailRolePrivilegeDTO> convertToRolePrivilegeDTO(Pageable pageable, String search) {
    Page<Map<String, Object>> listRoleID = rolePrivilegeRepository.findAllRoleId(pageable, search.toLowerCase());
    return listRoleID.map(rolePrivilegeMap -> {
      String test = Objects.toString(rolePrivilegeMap.get("role_id"));
      Integer roleId = Integer.parseInt(test);
      return convertToDTO(roleId);
    });
  }

  private DetailRolePrivilegeDTO convertToDTO(Integer roleIdInt) {
    CategoryCode roleId = categoryCodeRepository.findByCategoryCodeId((long) roleIdInt)
        .orElseThrow(() -> new EntityNotFoundException("Role ID Not Found!"));
    List<RolePrivilege> listRole = rolePrivilegeRepository.findByIdList(roleId);
    List<ListDetailPrivilegeDTO> list = listRole.stream().map(this::listDetailPrivilegeDTO)
        .collect(Collectors.toList());
    return DetailRolePrivilegeDTO.builder()
        .roleId(roleId.getCategoryCodeId())
        .roleName(roleId.getCodeName())
        .listPrivilege(list)
        .build();
  }

  private ListDetailPrivilegeDTO listDetailPrivilegeDTO(RolePrivilege rolePrivilege) {
    return ListDetailPrivilegeDTO.builder()
        .rolePrivilegeId(rolePrivilege.getRolePrivilegeId())
        .privilegeId(rolePrivilege.getPrivilegeId().getCategoryCodeId())
        .privilegeName(rolePrivilege.getPrivilegeId().getCodeName())
        .build();
  }

  public DetailRolePrivilegeDTO getDetailRolePrivilege(Long id) {
    CategoryCode roleId = categoryCodeRepository.findByCategoryCodeId(id)
        .orElseThrow(() -> new EntityNotFoundException("Role ID Not Found!"));
    List<RolePrivilege> listRolePrivilege = rolePrivilegeRepository.findByIdList(roleId);
    List<ListDetailPrivilegeDTO> detailRolePrivilegeDTO = listRolePrivilege.stream()
        .map(this::converListDetailPrivilegeDTO).collect(Collectors.toList());
    return DetailRolePrivilegeDTO.builder()
        .roleId(roleId.getCategoryCodeId())
        .roleName(roleId.getCodeName())
        .listPrivilege(detailRolePrivilegeDTO)
        .build();
  }

  private ListDetailPrivilegeDTO converListDetailPrivilegeDTO(RolePrivilege rolePrivilege) {
    return ListDetailPrivilegeDTO.builder()
        .rolePrivilegeId(rolePrivilege.getRolePrivilegeId())
        .privilegeId(rolePrivilege.getPrivilegeId().getCategoryCodeId())
        .privilegeName(rolePrivilege.getPrivilegeId().getCodeName())
        .build();
  }

  public RolePrivilege getRolePrivilegeId(Long rolePrivilegeId) {
    return rolePrivilegeRepository.findByRolePrivilegeId(rolePrivilegeId)
        .orElseThrow(() -> new EntityNotFoundException("Role Privilege ID Not Found!"));
  }

  CategoryCode getCategoryId(Long id) {
    return categoryCodeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Role Id Not Found!"));
  }

  @Transactional
  public RolePrivilege createRolePrivilege(AddRolePrivilegeDTO rolePrivilege) {
    try {
      Set<ConstraintViolation<AddRolePrivilegeDTO>> violations = validator.validate(rolePrivilege);
      if (!violations.isEmpty()) {
        List<String> errors = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
        throw new ValidationException(errors.get(0));
      }
      CategoryCode idRole = getCategoryId(rolePrivilege.getRoleId());
      List<Boolean> isPermission = checkPermission(rolePrivilege.getCreatedBy());
      List<RolePrivilege> existingRole = rolePrivilegeRepository.findByUserIdAndActive(idRole);
      RolePrivilege role = new RolePrivilege();
      if (isPermission.contains(true)) {
        if (existingRole.isEmpty()) {
          for (Long id : rolePrivilege.getListPrivilege()) {
            CategoryCode privilege = categoryCodeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Privilege Id Not Found!"));
            Optional<RolePrivilege> existingUserRole = rolePrivilegeRepository.findByUserRoleAndPrivilege(idRole,
                privilege);
            if (!existingUserRole.isPresent()) {
              role = RolePrivilege.builder()
                  .rolePrivilegeId(rolePrivilege.getRolePrivilegeId())
                  .roleId(idRole)
                  .privilegeId(privilege)
                  .active(true)
                  .createdBy(rolePrivilege.getCreatedBy())
                  .createdOn(timestampService.getUtcTimestamp())
                  .updateBy(rolePrivilege.getUpdatedBy())
                  .updateOn(timestampService.getUtcTimestamp())
                  .build();
              rolePrivilegeRepository.save(role);
            } else {
              existingUserRole.get().setActive(true);
              role = existingUserRole.get();
            }
          }
        } else {
          throw new EntityExistsException("Role Privilege Data Already Exist!");
        }
      } else {
        throw new ForbiddenException("You Don't Have Permission To Add Role Privilege!");
      }
      return role;
    } catch (Exception ex) {
      log.info(ex.getMessage());
      throw ex;
    }
  }

  public ResponseAddRolePrivilegeDTO convertToAddResponseRollPrivilege(RolePrivilege role) {
    List<RolePrivilege> rolePrivilege = rolePrivilegeRepository.findByIdList(role.getRoleId());
    List<ListDetailPrivilegeDTO> listPrivilege = rolePrivilege.stream().map(this::convertRolePrivilegeDTO)
        .collect(Collectors.toList());
    return ResponseAddRolePrivilegeDTO.builder()
        .rolePrivilegeId(role.getRolePrivilegeId())
        .roleId(role.getRoleId().getCategoryCodeId())
        .roleName(role.getRoleId().getCodeName())
        .listPrivilege(listPrivilege)
        .active(true)
        .createdBy(role.getCreatedBy())
        .createdOn(timestampService.getUtcTimestamp())
        .updatedBy(role.getUpdateBy())
        .updatedOn(timestampService.getUtcTimestamp())
        .build();
  }

  public ResponseUpdateRolePrivilegeDTO responseUpdateRolePrivilegeDTO(Long id) {
    CategoryCode categoryId = categoryCodeRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Role Not Found"));
    List<RolePrivilege> userRoles = rolePrivilegeRepository.findIdActive(categoryId);
    List<ListDetailPrivilegeDTO> userRoleDTOs = userRoles.stream()
        .map(this::convertRolePrivilegeDTO)
        .collect(Collectors.toList());

    return ResponseUpdateRolePrivilegeDTO.builder()
        .roleId(id)
        .roleName(categoryId.getCodeName())
        .privilegeId(userRoleDTOs)
        .build();
  }

  @Transactional
  public List<RolePrivilege> updateRolePrivilege(Long id, UpdateRolePrivilegeDTO userRoleDTO) {
    try {
      Set<ConstraintViolation<UpdateRolePrivilegeDTO>> violations = validator.validate(userRoleDTO);
      if (!violations.isEmpty()) {
        List<String> errors = violations.stream()
            .map(ConstraintViolation::getMessage)
            .collect(Collectors.toList());
        throw new ValidationException(errors.get(0));
      }
      CategoryCode categoryId = categoryCodeRepository.findById(id)
          .orElseThrow(() -> new EntityNotFoundException("Id Not Found"));
      List<RolePrivilege> userRoles = rolePrivilegeRepository.findByIdList(categoryId);
      List<Boolean> isPremision = checkPermission(userRoleDTO.getUpdatedBy());
      if (isPremision.contains(true)) {
        for (RolePrivilege uRole : userRoles) {
          uRole.setActive(false);
          uRole.setUpdateOn(timestampService.getUtcTimestamp());
        }
        rolePrivilegeRepository.saveAll(userRoles);
        updateRolePrivilege(id, userRoles, userRoleDTO);
      } else {
        throw new ForbiddenException("You Don't Have Permission To Update Role Privilege!");
      }
      return userRoles;
    } catch (Exception ex) {
      log.info(ex.getMessage());
      throw ex;
    }
  }

  @Transactional
  public void updateRolePrivilege(Long id, List<RolePrivilege> usersRole, UpdateRolePrivilegeDTO userRoleDTO) {
    try {
      for (int i = 0; i < userRoleDTO.getPrivilegeId().size(); i++) {
        CategoryCode categoryRoleId = categoryCodeRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Id Not Found"));
        Long privilegeId = userRoleDTO.getPrivilegeId().get(i);

        CategoryCode privilegeCode = categoryCodeRepository.findById(privilegeId)
            .orElseThrow(() -> new EntityNotFoundException("Privilege ID Not Found!"));

        Optional<RolePrivilege> existingUserRole = rolePrivilegeRepository.findByUserRoleAndPrivilege(categoryRoleId,
            privilegeCode);

        if (existingUserRole.isPresent()) {
          existingUserRole.get().setActive(true);
          existingUserRole.get().setUpdateOn(timestampService.getUtcTimestamp());
          existingUserRole.get().setUpdateBy(userRoleDTO.getUpdatedBy());
          rolePrivilegeRepository.save(existingUserRole.get());
        } else {
          RolePrivilege newUserRole = RolePrivilege.builder()
              .roleId(categoryRoleId)
              .privilegeId(privilegeCode)
              .active(true)
              .createdOn(timestampService.getUtcTimestamp())
              .updateOn(timestampService.getUtcTimestamp())
              .updateBy(userRoleDTO.getUpdatedBy())
              .createdBy(userRoleDTO.getUpdatedBy())
              .build();
          rolePrivilegeRepository.save(newUserRole);
        }
      }
    } catch (Exception ex) {
      log.info(ex.getMessage());
      throw ex;
    }
  }

  private ListDetailPrivilegeDTO convertRolePrivilegeDTO(RolePrivilege rolePrivilege) {
    return ListDetailPrivilegeDTO.builder()
        .rolePrivilegeId(rolePrivilege.getRoleId().getCategoryCodeId())
        .privilegeId(rolePrivilege.getPrivilegeId().getCategoryCodeId())
        .privilegeName(rolePrivilege.getPrivilegeId().getCodeName())
        .build();
  }

  private List<Boolean> checkPermission(Long userId) {
    User user = getService.getUser(userId, ConstantMessage.USER_NOT_FOUND);
    List<UserRole> listUserRole = userRoleRepository.findByIdAndActiveList(user);
    List<Boolean> isPermission = new ArrayList<>();
    for (UserRole uRole : listUserRole) {
      isPermission.add(userRoleRepository.isPermissionForUserRoleManipulation(uRole.getRoleId().getCategoryCodeId()));
    }
    return isPermission;
  }
}
