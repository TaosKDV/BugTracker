package ru.hepera.bug.tracker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "DEFECTS")
@Builder
@Getter
@Setter
@ToString
public class Defect {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "STATE", nullable = false)
  @Enumerated(EnumType.STRING)
  private DefectState state;

  @Column(name = "IMPORTANCE", nullable = false)
  @Enumerated(EnumType.STRING)
  private DefectImportance importance;

  @Column(name = "EXECUTOR_ID", nullable = false)
  private Long executorId;

  @Column(name = "AUTHOR_ID", nullable = false)
  private Long authorId;

  @Column(name = "FOUND_VERSION", nullable = false)
  private String foundVersion;

  @Column(name = "FIX_VERSION", nullable = false)
  private String fixVersion;

}
