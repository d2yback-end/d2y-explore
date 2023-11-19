package com.d2y.d2yapiofficial;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.d2y.d2yapiofficial.controllers.UserController;

@SpringBootTest
class D2yApiOfficialApplicationTests {

  @Autowired
  private UserController userController;

  @Test
  void contextLoads() {
    assertNotNull(userController);
  }

}
