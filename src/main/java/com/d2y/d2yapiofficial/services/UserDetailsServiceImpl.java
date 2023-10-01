package com.d2y.d2yapiofficial.services;

import static java.util.Collections.singletonList;

import java.util.Collection;
import java.util.Optional;

import javax.persistence.EntityExistsException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.d2y.d2yapiofficial.models.User;
import com.d2y.d2yapiofficial.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService {
  private final UserRepository userRepository;

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String email) {
    Optional<User> userOptional = userRepository.findByEmail(email);
    User user = userOptional
        .orElseThrow(() -> new EntityExistsException("User Not Found!"));

    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
        user.isEnabled(),
        true, true,
        true,
        getAuthorities("USER"));
  }

  private Collection<? extends GrantedAuthority> getAuthorities(String role) {
    return singletonList(new SimpleGrantedAuthority(role));
  }
}