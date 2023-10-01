package com.d2y.d2yapiofficial.controllers;

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

import com.d2y.d2yapiofficial.dto.privilege.AddRolePrivilegeDTO;
import com.d2y.d2yapiofficial.dto.privilege.DetailRolePrivilegeDTO;
import com.d2y.d2yapiofficial.dto.privilege.UpdateRolePrivilegeDTO;
import com.d2y.d2yapiofficial.services.RolePrivilegeService;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;
import com.toedter.spring.hateoas.jsonapi.JsonApiModelBuilder;

import lib.i18n.utility.MessageUtil;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/role-privilege")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RolePrivilegeController {
  private final RolePrivilegeService rolePrivilegeService;
  private final MessageUtil msg;

  @GetMapping
  public ResponseEntity<Object> getAllRolePrivilege(Pageable pageable, @RequestParam(required = false) String search) {
    Page<DetailRolePrivilegeDTO> listRoleDTO = rolePrivilegeService.convertToRolePrivilegeDTO(pageable, search);
    return ResponseEntity.ok(PagedModel.of(listRoleDTO.getContent(),
        new PageMetadata(listRoleDTO.getSize(), listRoleDTO.getNumber(), listRoleDTO.getTotalElements())));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getDetailRolePrivilege(@PathVariable("id") Long id) {
    var drp = rolePrivilegeService.getDetailRolePrivilege(id);
    return ResponseEntity.ok(EntityModel.of(drp));
  }

  @PostMapping("/add")
  public ResponseEntity<Object> addRolePrivilege(@RequestBody AddRolePrivilegeDTO role) {
    var o = rolePrivilegeService.createRolePrivilege(role);
    var d = rolePrivilegeService.convertToAddResponseRollPrivilege(o);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(JsonApiModelBuilder
            .jsonApiModel()
            .model(EntityModel.of(d))
            .meta(ConstantMessage.MESSAGE, msg
                .get(ConstantMessage.APP_SUCCESS_CREATED, ConstantMessage.ROLE_PRIVILEGE))
            .build());
  }

  @PutMapping("/update/{id}")
  public ResponseEntity<Object> updateRolePrivilege(@PathVariable("id") Long id,
      @RequestBody UpdateRolePrivilegeDTO rolePrivilegeDTO) {
    rolePrivilegeService.updateRolePrivilege(id, rolePrivilegeDTO);
    var o = rolePrivilegeService.responseUpdateRolePrivilegeDTO(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(JsonApiModelBuilder
            .jsonApiModel()
            .model(EntityModel.of(o))
            .meta(ConstantMessage.MESSAGE, msg
                .get(ConstantMessage.APP_SUCCESS_UPDATED, ConstantMessage.ROLE_PRIVILEGE))
            .build());
  }

  @DeleteMapping("/delete/{id}")
  public ResponseEntity<Object> deleteRolePrivilege(@PathVariable("id") Long id) {
    rolePrivilegeService.deleteRolePrivilege(id);
    return ResponseEntity.status(HttpStatus.OK)
        .body(JsonApiModelBuilder.jsonApiModel()
            .meta(ConstantMessage.MESSAGE, msg
                .get(ConstantMessage.APP_SUCCESS_DELETED, ConstantMessage.ROLE_PRIVILEGE))
            .build());
  }
}
