package com.d2y.d2yapiofficial.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.d2y.d2yapiofficial.dto.user.UpdateUserDTO;
import com.d2y.d2yapiofficial.dto.user.UserResponseDTO;
import com.d2y.d2yapiofficial.models.CategoryCode;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.repositories.UserRepository;
import com.d2y.d2yapiofficial.repositories.UserRoleRepository;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;

@SpringBootTest
public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserRoleRepository userRoleRepository;

  @Mock
  private GetService getService;

  @Test
  public void testGetAllUsers() {
    // Mock data for userRepository.getListUsers
    User user1 = new User();
    user1.setUserId(5L);
    user1.setUsername("user1");

    User user2 = new User();
    user2.setUserId(7L);
    user2.setUsername("user2");

    List<User> userList = new ArrayList<>();
    userList.add(user1);
    userList.add(user2);

    // Mock the behavior of userRepository.getListUsers
    Pageable pageable = Pageable.unpaged();
    when(userRepository.getListUsers(any(String.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(userList, pageable, userList.size())
            .map(user -> userService.buildUserDTO(user))); // Map to UserResponseDTO

    // Call the method to test
    Page<UserResponseDTO> result = userService.getAllUsers(pageable, "");

    // Assertions
    assertNotNull(result);
    assertEquals(2, result.getContent().size());
    assertEquals("user1", result.getContent().get(0).getUsername());
    assertEquals("user2", result.getContent().get(1).getUsername());
  }

  @Test
  public void testGetUserById() {
    // Mock data for getUser
    User user = new User();
    user.setUserId(7L);
    user.setUsername("user1");

    // Mock the behavior of getService.getUser
    when(getService.getUser(7L, ConstantMessage.USER_NOT_FOUND)).thenReturn(user);

    // Call the method to test
    UserResponseDTO result = userService.getUserById(7L);

    // Assertions
    assertNotNull(result);
    assertEquals(7L, result.getUserId());
    assertEquals("user1", result.getUsername());
  }

  @Test
  public void testUpdateUser() {
    // Mock data for getUser
    User existingUser = new User();
    existingUser.setUserId(7L);
    existingUser.setUsername("user1");

    // Mock the behavior of getService.getUser
    when(getService.getUser(7L, ConstantMessage.USER_NOT_FOUND)).thenReturn(existingUser);

    // Mock data for updateUserDTO
    UpdateUserDTO updateUserDTO = new UpdateUserDTO();
    updateUserDTO.setUsername("user1-update");

    // Call the method to test
    User updatedUser = userService.updateUser(7L, updateUserDTO);

    // Assertions
    assertNotNull(updatedUser);
    assertEquals("user1-update", updatedUser.getUsername());
  }

  @Test
  public void testCheckPermission() {
    // Mock data for getUser
    User user = new User();
    user.setUserId(7L);
    user.setUsername("user1");

    // Mock the behavior of getService.getUser
    when(getService.getUser(7L, ConstantMessage.USER_NOT_FOUND)).thenReturn(user);

    CategoryCode categoryCodeId1 = getService.getCategoryCode(1L, ConstantMessage.CATEGORY_CODE_NOT_FOUND);
    CategoryCode categoryCodeId2 = getService.getCategoryCode(2L, ConstantMessage.CATEGORY_CODE_NOT_FOUND);
    // Mock data for userRoleRepository.findByIdAndActiveList
    UserRole userRole1 = new UserRole();
    userRole1.setUserRoleId(1L);
    userRole1.setRoleId(categoryCodeId1);

    UserRole userRole2 = new UserRole();
    userRole2.setUserRoleId(2L);
    userRole2.setRoleId(categoryCodeId2);

    List<UserRole> userRoles = new ArrayList<>();
    userRoles.add(userRole1);
    userRoles.add(userRole2);

    when(userRoleRepository.findByIdAndActiveList(user)).thenReturn(userRoles);

    // Mock the behavior of userRoleRepository.isPermissionForUserRoleManipulation
    when(userRoleRepository.isPermissionForUserRoleManipulation(1L)).thenReturn(true);
    when(userRoleRepository.isPermissionForUserRoleManipulation(2L)).thenReturn(false);

    // Call the method to test
    List<Boolean> permissions = userService.checkPermission(7L);

    // Assertions
    assertNotNull(permissions);
    assertEquals(2, permissions.size());
    assertTrue(permissions.get(0));
    assertFalse(permissions.get(1));
  }
}
