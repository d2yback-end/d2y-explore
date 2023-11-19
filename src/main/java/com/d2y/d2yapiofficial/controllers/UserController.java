package com.d2y.d2yapiofficial.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.d2y.d2yapiofficial.dto.user.UpdateUserDTO;
import com.d2y.d2yapiofficial.dto.user.UserResponseDTO;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.services.GetService;
import com.d2y.d2yapiofficial.services.UserService;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import lib.i18n.utility.MessageUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {

  private final UserService userService;
  private final GetService getService;
  private final MessageUtil messageUtil;

  @GetMapping
  public Object getAllUsers(Pageable pageable, @RequestParam(required = false) String search) {
    Page<UserResponseDTO> users = userService.getAllUsers(pageable, search);
    List<UserResponseDTO> userDTO = users.getContent().stream().collect(Collectors.toList());
    return ResponseEntity.ok(PagedModel.of(userDTO,
        new PageMetadata(users.getSize(), users.getNumber(),
            users.getTotalElements())));
  }

  @GetMapping("/{userId}")
  public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
    UserResponseDTO userDTO = userService.getUserById(userId);

    return ResponseEntity.status(HttpStatus.OK).body(EntityModel.of(userDTO));
  }

  @PutMapping("/{userId}")
  public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UpdateUserDTO updateUserDTO) {
    userService.updateUser(userId, updateUserDTO);
    return ResponseEntity.status(HttpStatus.OK)
        .body(JsonApiModelBuilder
            .jsonApiModel()
            .meta(ConstantMessage.MESSAGE, messageUtil
                .get(ConstantMessage.APP_SUCCESS_UPDATED, ConstantMessage.USER))
            .build());
  }

  @DeleteMapping("/{userId}")
  public ResponseEntity<Object> deleteUser(HttpServletRequest request, @PathVariable Long userId) {
    userService.deleteUser(getService.getToken(request), userId);
    return ResponseEntity.status(HttpStatus.OK)
        .body(JsonApiModelBuilder
            .jsonApiModel()
            .meta(ConstantMessage.MESSAGE, messageUtil
                .get(ConstantMessage.APP_SUCCESS_DELETED, ConstantMessage.USER))
            .build());
  }
}