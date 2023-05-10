package ru.hepera.bug.tracker.model;

import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "USERS")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID", nullable = false)
  private long id;

  @Column(name = "PASSWORD", nullable = false)
  private String password;

  @Column(name = "USERNAME", nullable = false)
  private String username;

  @Column(name = "ENABLED", nullable = false)
  private boolean active;

  @Column(name = "ROLES")
  @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"))
  @Enumerated(EnumType.STRING)
  private Set<Role> roles;

}
