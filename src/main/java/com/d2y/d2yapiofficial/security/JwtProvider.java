package com.d2y.d2yapiofficial.security;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.dto.privilege.PrivilegeDTO;
import com.d2y.d2yapiofficial.dto.role.RoleDTO;
import com.d2y.d2yapiofficial.models.RolePrivilege;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.repositories.RolePrivilegeRepository;
import com.d2y.d2yapiofficial.repositories.UserRoleRepository;
import com.d2y.d2yapiofficial.services.GetService;
import com.d2y.d2yapiofficial.utils.constants.ConstantMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtProvider {

  private final JwtEncoder jwtEncoder;
  private final JwtDecoder jwtDecoder;
  private final UserRoleRepository userRoleRepository;
  private final RolePrivilegeRepository rolePrivilegeRepository;
  private final GetService getService;

  @Value("${jwt.secret.key}")
  private String secretKey;

  @Value("${jwt.issuer}")
  private String jwtIssuer;

  @Value("${jwt.expiration}")
  private Long jwtExpirationInMillis;

  public String generateToken(Authentication authentication) {
    User principal = (User) authentication.getPrincipal();
    return generateTokenWithUserName(principal.getUsername());
  }

  public String generateTokenWithUserName(String email) {
    com.d2y.d2yapiofficial.models.User user = getService.getUserByEmail(email, ConstantMessage.USER_NOT_FOUND);
    List<UserRole> listRole = userRoleRepository.findByIdAndActiveList(user);

    List<RoleDTO> roleNames = listRole.stream()
        .map(this::convertRoleDTO)
        .collect(Collectors.toList());
    List<PrivilegeDTO> privilegeNames = convertPrivilegeDTO(listRole);

    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer(jwtIssuer)
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusMillis(jwtExpirationInMillis))
        .subject(email)
        .claim("roles", roleNames)
        .claim("privileges", privilegeNames)
        .build();

    return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
  }

  public Long getJwtExpirationInMillis() {
    return jwtExpirationInMillis;
  }

  public String getEmailFromToken(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    return jwt.getSubject();
  }

  public List<String> getRolesFromToken(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    List<String> roles = jwt.getClaimAsStringList("roles");

    if (roles == null) {
      return Collections.emptyList();
    }

    return roles;
  }

  public List<String> getPrivilegesFromToken(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    List<String> privileges = jwt.getClaimAsStringList("privileges");

    if (privileges == null) {
      return Collections.emptyList();
    }

    return privileges;
  }

  // public List<String> getPrivilegesFromToken(String token) {
  // Jwt jwt = jwtDecoder.decode(token);
  // return jwt.getClaim("privileges");
  // }

  public RoleDTO convertRoleDTO(UserRole userRole) {
    return RoleDTO.builder()
        .roleId(userRole.getRoleId().getCategoryCodeId())
        .roleName(userRole.getRoleId().getCodeName())
        .build();
  }

  private List<PrivilegeDTO> convertPrivilegeDTO(List<UserRole> listUserRole) {
    List<PrivilegeDTO> listPrivilege = new ArrayList<>();
    for (UserRole role : listUserRole) {
      List<RolePrivilege> rolePrivilege = rolePrivilegeRepository.getListRolePrivilege(role.getRoleId());
      for (RolePrivilege rPrivilege : rolePrivilege) {
        PrivilegeDTO buildRolePrivilege = PrivilegeDTO.builder()
            .privilegeId(rPrivilege.getPrivilegeId().getCategoryCodeId())
            .privilegeName(rPrivilege.getPrivilegeId().getCodeName())
            .build();
        if (!listPrivilege.contains(buildRolePrivilege)) {
          listPrivilege.add(buildRolePrivilege);
        }
      }
    }
    return listPrivilege;
  }
}