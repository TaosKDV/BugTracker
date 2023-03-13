package ru.hepera.bug.tracker.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AuthenticationForm {

  private String repass;

  public AuthenticationForm(){
  }
}
