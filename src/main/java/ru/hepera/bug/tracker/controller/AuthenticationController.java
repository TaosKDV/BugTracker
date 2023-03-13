package ru.hepera.bug.tracker.controller;

import java.util.Collections;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.hepera.bug.tracker.form.AuthenticationForm;
import ru.hepera.bug.tracker.model.Role;
import ru.hepera.bug.tracker.model.User;
import ru.hepera.bug.tracker.repository.UserRepository;

@Controller
@Log4j2
public class AuthenticationController {

  private final String REASS_INVALID_ERROR_MESSAGE = "Пароли не совпадают!";

  private final String LOGIN_IN_USE_ERROR_MESSAGE = "Логин занят введите другой!";

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @GetMapping({"/", "/auth", "/login"})
  public String auth(Model model) {
    AuthenticationForm authenticationForm = new AuthenticationForm();
    model.addAttribute("authenticationForm", authenticationForm);
    return "auth.html";
  }

  private String errorMessage(Model model, String errorMessage) {
    model.addAttribute("errorMessage", errorMessage);
    return "auth.html";
  }

  @PostMapping({"/reg"})
  public String reg(Model model, String repass, User user) {
    log.info("Создаем пользователя");
    if (!repass.equals(user.getPassword())) {
      log.error("pass и repass не совпадают!");
      return errorMessage(model, REASS_INVALID_ERROR_MESSAGE);
    }
    User userFromDb = userRepository.findByUsername(user.getUsername());
    if (userFromDb != null) {
      return errorMessage(model, LOGIN_IN_USE_ERROR_MESSAGE);
    }
    user.setActive(true);
    user.setRoles(Collections.singleton(Role.USER));
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    userRepository.save(user);
    return "auth.html";
  }
}
