package com.d2y.d2yapiofficial.services;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.d2y.d2yapiofficial.dto.auth.AuthResponse;
import com.d2y.d2yapiofficial.dto.auth.LoginRequest;
import com.d2y.d2yapiofficial.dto.auth.RefreshTokenRequest;
import com.d2y.d2yapiofficial.dto.auth.RegisterRequest;
import com.d2y.d2yapiofficial.dto.privilege.PrivilegeDTO;
import com.d2y.d2yapiofficial.dto.role.RoleDTO;
import com.d2y.d2yapiofficial.exceptions.ForbiddenException;
import com.d2y.d2yapiofficial.models.NotificationEmail;
import com.d2y.d2yapiofficial.models.RolePrivilege;
import com.d2y.d2yapiofficial.models.Token;
import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.models.UserRole;
import com.d2y.d2yapiofficial.repositories.RolePrivilegeRepository;
import com.d2y.d2yapiofficial.repositories.TokenRepository;
import com.d2y.d2yapiofficial.repositories.UserRepository;
import com.d2y.d2yapiofficial.repositories.UserRoleRepository;
import com.d2y.d2yapiofficial.security.JwtProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthService {

  private final MailService mailService;
  private final TokenRepository tokenRepository;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final RolePrivilegeRepository rolePrivilegeRepository;
  private final RefreshTokenService refreshTokenService;
  private final PasswordEncoder passwordEncoder;
  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;

  @Transactional
  public void registerUser(RegisterRequest registrationDto) {
    try {
      validateRegistrationInput(registrationDto);

      User user = createUserFromRequest(registrationDto);
      user = userRepository.save(user);

      sendVerificationEmail(user);
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  @Transactional
  public AuthResponse login(LoginRequest loginRequest) {
    try {
      User user = getUserByEmail(loginRequest.getEmail());
      validateUserEnabled(user, loginRequest);

      Authentication authenticate = authenticationManager
          .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
              loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authenticate);

      user.setLastLogin(new Timestamp(System.currentTimeMillis()));
      userRepository.save(user);

      return createAuthResponse(user, authenticate);
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  @Transactional
  public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
    try {
      refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
      String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getEmail());

      return AuthResponse.builder()
          .accessToken(token)
          .refreshToken(refreshTokenService.generateRefreshToken().getToken())
          .build();
    } catch (Exception ex) {
      log.info(ex.getMessage());
      ex.printStackTrace();
      throw ex;
    }
  }

  // Function for register

  private void validateRegistrationInput(RegisterRequest registrationDto) {
    if (userRepository.existsByEmail(registrationDto.getEmail())) {
      throw new EntityExistsException("Email already exists!");
    }

    if (registrationDto.getPassword().length() < 8) {
      throw new ValidationException("Password must be at least 8 characters long.");
    }

    if (!isStrongPassword(registrationDto.getPassword())) {
      throw new ValidationException(
          "Password must contain a combination of uppercase letters, lowercase letters, numbers, and special characters.");
    }
  }

  private User createUserFromRequest(RegisterRequest registrationDto) {
    User user = new User();
    user.setUsername(registrationDto.getUsername());
    user.setEmail(registrationDto.getEmail());
    user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
    user.setCreatedOn(new Timestamp(System.currentTimeMillis()));
    user.setUpdatedOn(new Timestamp(System.currentTimeMillis()));
    user.setRegistrationDate(new Timestamp(System.currentTimeMillis()));
    user.setActive(true);
    user.setEnabled(false);
    return user;
  }

  private boolean isStrongPassword(String password) {
    return password.matches("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%!*+=_^&\\-\\[\\]{}\\/?.,><\\\\|]).{8,}$");
  }

  private void sendVerificationEmail(User user) {
    NotificationEmail mailMessage = createNotificationEmail(user);
    mailService.sendMail(mailMessage);
  }

  private NotificationEmail createNotificationEmail(User user) {
    String recipientEmail = user.getEmail();
    String subject = "Welcome " + user.getUsername();
    String token = generateVerificationToken(user);
    String verificationUrl = "http://localhost:5000/api/v1/auth/accountVerification/" + token;

    NotificationEmail notificationEmail = new NotificationEmail();
    notificationEmail.setRecipient(recipientEmail);
    notificationEmail.setSubject(subject);
    notificationEmail.setUsername(user.getUsername());
    notificationEmail.setVerificationUrl(verificationUrl);

    return notificationEmail;
  }

  private String generateVerificationToken(User user) {
    String token = UUID.randomUUID().toString();
    Token verificationToken = new Token();
    verificationToken.setToken(token);
    verificationToken.setUser(user);

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime expiryDateTime = now.plusHours(24);
    verificationToken.setExpiryDate(Timestamp.valueOf(expiryDateTime));
    verificationToken.setExpired(false);
    verificationToken.setCreatedOn(new Timestamp(System.currentTimeMillis()));

    tokenRepository.save(verificationToken);
    return token;
  }

  // Function for login

  private User getUserByEmail(String email) {
    return userRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));
  }

  private void validateUserEnabled(User user, LoginRequest loginRequest) {
    if (!user.isEnabled()) {
      throw new ForbiddenException("Please check your email to verify your account.");
    }

    if (!isPasswordValid(loginRequest.getPassword(), user.getPassword())) {
      throw new ValidationException("Invalid email or password.");
    }
  }

  private boolean isPasswordValid(String rawPassword, String hashedPasswordFromDb) {
    return passwordEncoder.matches(rawPassword, hashedPasswordFromDb);
  }

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

  private AuthResponse createAuthResponse(User user, Authentication authentication) {
    String token = jwtProvider.generateToken(authentication);
    List<UserRole> listRole = userRoleRepository.findByIdAndActiveList(user);

    return AuthResponse.builder()
        .userId(user.getUserId())
        .email(user.getEmail())
        .username(user.getUsername())
        .listRole(listRole.stream().map(this::convertRoleDTO).collect(Collectors.toList()))
        .listPrivilege(convertPrivilegeDTO(listRole))
        .accessToken(token)
        .refreshToken(refreshTokenService.generateRefreshToken().getToken())
        .build();
  }

  // Another function

  public boolean verifyAccount(String token) {
    Optional<Token> verificationToken = tokenRepository.findByToken(token);
    return fetchUserAndEnable(
        verificationToken.orElseThrow(() -> new ValidationException("Invalid Verification Token")));
  }

  private boolean fetchUserAndEnable(Token verificationToken) {
    if (verificationToken.isExpired()) {
      return false;
    }

    Long userId = verificationToken.getUser().getUserId();
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new EntityNotFoundException("User not found"));

    user.setEnabled(true);
    userRepository.save(user);
    tokenRepository.delete(verificationToken);
    return true;
  }

  @Transactional
  public User getCurrentUser() {
    Jwt principal = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userRepository.findByEmail(principal.getSubject())
        .orElseThrow(() -> new EntityNotFoundException("User Not Found"));
  }
}
