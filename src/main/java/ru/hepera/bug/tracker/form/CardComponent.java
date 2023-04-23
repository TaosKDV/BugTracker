package ru.hepera.bug.tracker.form;

import lombok.Builder;
import lombok.Getter;
import ru.hepera.bug.tracker.model.DefectImportance;
import ru.hepera.bug.tracker.model.DefectState;

@Builder
@Getter
public class CardComponent {

  private Long id;

  private String name;

  private DefectState state;

  private DefectImportance importance;

  private String executor;

  private String author;

}
