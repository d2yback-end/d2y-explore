package com.d2y.d2yapiofficial.services;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.dto.user.UpdateUserDTO;
import com.d2y.d2yapiofficial.dto.user.UserResponseDTO;
import com.d2y.d2yapiofficial.exceptions.ForbiddenException;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.repositories.UserRepository;
import com.d2y.d2yapiofficial.repositories.UserRoleRepository;
import com.d2y.d2yapiofficial.security.JwtProvider;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

  private final UserRepository userRepository;
  private final GetService getService;
  private final JwtProvider jwtProvider;
  private final TimestampService timestampService;
  private final UserRoleRepository userRoleRepository;

  public Page<UserResponseDTO> getAllUsers(Pageable pageable, String search) {
    return userRepository.getListUsers(search.toLowerCase(), pageable);
  }

  public User updateUser(Long userId, UpdateUserDTO updateUserDTO) {
    User existingUser = getService.getUser(userId, ConstantMessage.USER_NOT_FOUND);
    updateFields(existingUser, updateUserDTO);
    existingUser.setUpdatedOn(timestampService.getUtcTimestamp());

    return userRepository.save(existingUser);
  }

  public void deleteUser(String token, Long userId) {
    String email = jwtProvider.getEmailFromToken(token);
    User updatedBy = getService.getUserByEmail(email, ConstantMessage.USER_NOT_FOUND);
    User user = getService.getUser(userId, ConstantMessage.USER_NOT_FOUND);

    User userActive = userRepository.findByIdAndActive(user.getUserId())
        .orElseThrow(() -> new EntityNotFoundException(ConstantMessage.USER_NOT_FOUND));
    List<Boolean> checkPermission = checkPermission(updatedBy.getUserId());
    if (checkPermission.contains(true)) {
      userActive.setActive(false);
      userActive.setUpdatedOn(timestampService.getUtcTimestamp());

      userRepository.save(user);
    } else {
      throw new ForbiddenException("You Don't Have Permission To Delete User!");
    }

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

  public UserResponseDTO buildUserDTO(User user) {
    return UserResponseDTO.builder()
        .userId(user.getUserId())
        .username(user.getUsername())
        .email(user.getEmail())
        .phoneNumber(user.getPhoneNumber())
        .photoProfile(user.getPhotoProfile())
        .createdOn(user.getCreatedOn())
        .updatedOn(user.getUpdatedOn())
        .registrationDate(user.getRegistrationDate())
        .lastLogin(user.getLastLogin())
        .active(user.isActive())
        .enabled(user.isEnabled())
        .bio(user.getBio())
        .igAccount(user.getIgAccount())
        .twitterAccount(user.getTwitterAccount())
        .fbAccount(user.getFbAccount())
        .ytAccount(user.getYtAccount())
        .address(user.getAddress())
        .website(user.getWebsite())
        .gender(user.getGender())
        // .dateOfBirth(user.getDateOfBirth())
        .build();
  }

  private void updateFields(User existingUser, UpdateUserDTO updateUserDTO) {
    if (updateUserDTO.getUsername() != null) {
      existingUser.setUsername(updateUserDTO.getUsername());
    }
    if (updateUserDTO.getDateOfBirth() != null) {
      existingUser.setDateOfBirth(updateUserDTO.getDateOfBirth());
    }
    if (updateUserDTO.getPhoneNumber() != null) {
      existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
    }
    if (updateUserDTO.getPhotoProfile() != null) {
      existingUser.setPhotoProfile(updateUserDTO.getPhotoProfile());
    }
    if (updateUserDTO.getBio() != null) {
      existingUser.setBio(updateUserDTO.getBio());
    }
    if (updateUserDTO.getIgAccount() != null) {
      existingUser.setIgAccount(updateUserDTO.getIgAccount());
    }
    if (updateUserDTO.getTwitterAccount() != null) {
      existingUser.setTwitterAccount(updateUserDTO.getTwitterAccount());
    }
    if (updateUserDTO.getFbAccount() != null) {
      existingUser.setFbAccount(updateUserDTO.getFbAccount());
    }
    if (updateUserDTO.getYtAccount() != null) {
      existingUser.setYtAccount(updateUserDTO.getYtAccount());
    }
    if (updateUserDTO.getAddress() != null) {
      existingUser.setAddress(updateUserDTO.getAddress());
    }
    if (updateUserDTO.getWebsite() != null) {
      existingUser.setWebsite(updateUserDTO.getWebsite());
    }
    if (updateUserDTO.getGender() != null) {
      existingUser.setGender(updateUserDTO.getGender());
    }
  }
}