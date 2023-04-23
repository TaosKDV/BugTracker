package ru.hepera.bug.tracker.model;

import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "DEFECTS")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Defect {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Column(name = "NAME")
  private String name;

  @Column(name = "STATE", nullable = false)
  @Enumerated(EnumType.STRING)
  private DefectState state;

  @Column(name = "IMPORTANCE", nullable = false)
  @Enumerated(EnumType.STRING)
  private DefectImportance importance;

  @Column(name = "EXECUTOR_ID")
  private Long executorId;

  @Column(name = "AUTHOR_ID")
  private Long authorId;

  @Column(name = "FOUND_VERSION")
  private String foundVersion;

  @Column(name = "FIX_VERSION")
  private String fixVersion;

  @Column(name = "STEPS")
  @Lob
  private String steps;

  @Column(name = "EXPECTED_RESULT")
  @Lob
  private String expectedResult;

  @Column(name = "ACTUAL_RESULT")
  @Lob
  private String actualResult;

  @Column(name = "ATTACHMENTS")
  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "DEFECT_ID")
  private Set<Attachment> attachments;

}