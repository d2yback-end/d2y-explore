package com.d2y.d2yapiofficial.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.d2y.d2yapiofficial.dto.user.UpdateUserDTO;
import com.d2y.d2yapiofficial.dto.user.UserResponseDTO;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.services.GetService;
import com.d2y.d2yapiofficial.services.UserService;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import lib.i18n.utility.MessageUtil;

@WebMvcTest(UserController.class)
class UserControllerTest {

  final String token = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJhZGlAZ21haWwuY29tIiwicHJpdmlsZWdlcyI6W10sInJvbGVzIjpbeyJyb2xlSWQiOjIsInJvbGVOYW1lIjoiTW9kZXJhdG9yIn0seyJyb2xlSWQiOjMsInJvbGVOYW1lIjoiVXNlciJ9LHsicm9sZUlkIjoxLCJyb2xlTmFtZSI6IkFkbWluaXN0cmF0b3IifV0sImlzcyI6Imh0dHA6XC9cL2xvY2FsaG9zdDo1MDAwIiwiZXhwIjoxNjk2NDMzODMzLCJpYXQiOjE2OTYzNDc0MzN9.zNbJ6OBmxNWW4mQceZ4T7jG-LlvZkRkNCIrWuEvb0xmUDqjxdr99W9QXC3_xnhbsARumpn2NhA-esGu4upnEkyma0fw1TJNaH-du-4bEKoyP9KzhspL0J5z-UtiGEPXUGYfzARN4vrtfWzRWWV_SxFNrF6C0Db_IAhnpTet5ODbLf_9mUFqnC7xVoiZwXAsAQxMhugsGgxl7bgap8jXbQ1aOzHFxAus2Go4qrlsYdz9JK6QnEHCAXJVeAUvfCFoYALP05xu6uR8j1XQzXjylaerKopjXZ00XPXU-sqXxkZqpBe8_gJsTc2UOIKX4cG1yoMYkSt0UKynrpxUg8D25Vg";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private GetService getService;

  @MockBean
  private MessageUtil messageUtil;

  @MockBean
  private UserDetailsService userDetailsService;

  @Test
  void testGetAllUsers() throws Exception {
    // Mock the userService.getAllUsers() method
    List<UserResponseDTO> userDTOList = new ArrayList<>();
    userDTOList.add(new UserResponseDTO());
    PageImpl<UserResponseDTO> userPage = new PageImpl<>(userDTOList, PageRequest.of(0, 10), 1);
    when(userService.getAllUsers(any(), any())).thenReturn(userPage);

    mockMvc.perform(get("/api/v1/users"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testGetUserById() throws Exception {
    // Mock the getService.getUser() method
    Long userId = 5L;
    String username = "d2y";

    User user = new User();
    user.setUserId(userId);
    user.setUsername(username);

    UserResponseDTO userDTO = new UserResponseDTO(user);
    when(getService.getUser(userId, ConstantMessage.USER_NOT_FOUND)).thenReturn(user);
    when(userService.buildUserDTO(user)).thenReturn(userDTO);

    mockMvc.perform(get("/api/v1/users/{userId}", userId))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testUpdateUser() throws Exception {
    // Mock the userService.updateUser() method
    Long userId = 1L;
    UpdateUserDTO updateUserDTO = new UpdateUserDTO();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
    ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    String requestBody = objectWriter.writeValueAsString(updateUserDTO);

    mockMvc.perform(put("/api/v1/users/{userId}", userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void testDeleteUser() throws Exception {
    Long userId = 5L;
    when(getService.getToken(any(HttpServletRequest.class))).thenReturn(token);

    mockMvc.perform(delete("/api/v1/users/{userId}", userId)
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }
}
