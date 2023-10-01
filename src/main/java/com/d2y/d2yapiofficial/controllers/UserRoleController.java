package com.d2y.d2yapiofficial.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.d2y.d2yapiofficial.dto.role.AddUserRoleDTO;
import com.d2y.d2yapiofficial.dto.role.DetailUserRoleDTO;
import com.d2y.d2yapiofficial.dto.role.UpdateUserRoleDTO;
import com.d2y.d2yapiofficial.services.GetService;
import com.d2y.d2yapiofficial.services.UserRoleService;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import lib.i18n.utility.MessageUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/user-role")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRoleController {

  private final GetService getService;
  private final UserRoleService userRoleService;
  private final MessageUtil messageUtil;

  @GetMapping
  public ResponseEntity<Object> getAllUserRole(Pageable pageable, @RequestParam(required = false) String search) {
    Page<DetailUserRoleDTO> userRolePage = userRoleService.convertToRolePrivilegeDTO(pageable, search);
    return ResponseEntity.ok(PagedModel.of(userRolePage.getContent(),
        new PageMetadata(userRolePage.getSize(), userRolePage.getNumber(), userRolePage.getTotalElements())));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getDetailUserRole(@PathVariable("id") Long id) {
    var o = userRoleService.getDetailUserRole(id);
    return ResponseEntity.ok(EntityModel.of(o));
  }

  @PostMapping("/add")
  public ResponseEntity<Object> addUserRole(@RequestBody AddUserRoleDTO userRoleDto) throws Exception {
    var o = userRoleService.addUserRole(userRoleDto);
    var d = userRoleService.convertAddUserRoleDTO(o);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(JsonApiModelBuilder
            .jsonApiModel()
            .model(EntityModel.of(d))
            .meta(ConstantMessage.MESSAGE, messageUtil
                .get(ConstantMessage.APP_SUCCESS_CREATED, ConstantMessage.USER_ROLE))
            .build());
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateUserRole(@PathVariable("id") Long id,
      @RequestBody UpdateUserRoleDTO userRoleDto) throws Exception {
    userRoleService.updateUserRole(id, userRoleDto);
    var d = userRoleService.convertUpdateUserRoleDTO(id);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(JsonApiModelBuilder
            .jsonApiModel()
            .model(EntityModel.of(d))
            .meta(ConstantMessage.MESSAGE, messageUtil
                .get(ConstantMessage.APP_SUCCESS_UPDATED, ConstantMessage.USER_ROLE))
            .build());
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Object> deleteUserRole(HttpServletRequest request, @PathVariable("id") Long id) {
    userRoleService.deleteRoleUser(getService.getToken(request), id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(JsonApiModelBuilder
            .jsonApiModel()
            .meta(ConstantMessage.MESSAGE, messageUtil
                .get(ConstantMessage.APP_SUCCESS_DELETED, ConstantMessage.USER_ROLE))
            .build());
  }
}
