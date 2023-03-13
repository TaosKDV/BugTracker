package ru.hepera.bug.tracker.form;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.hepera.bug.tracker.model.DefectImportance;
import ru.hepera.bug.tracker.model.DefectState;
import ru.hepera.bug.tracker.model.User;

@Getter
@Setter
@ToString
public class NewDefectForm {

  private String name;

  private DefectState state;

  private DefectImportance importance;

  private Long executorId;

  private User author;

  private String foundVersion;

  private String fixVersion;

}
