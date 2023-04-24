package ru.hepera.bug.tracker.form;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;
import ru.hepera.bug.tracker.model.DefectImportance;
import ru.hepera.bug.tracker.model.DefectState;
import ru.hepera.bug.tracker.model.User;

@Getter
@Setter
@ToString
@Builder
public class DefectForm {

  private Long defectId;

  private String name;

  private DefectState state;

  private DefectImportance importance;

  private List<User> users;

  private Long executorId;

  private Long authorId;

  private String foundVersion;

  private String fixVersion;

  private String steps;

  private String expectedResult;

  private String actualResult;

  private String[] attachmentNames;

  private MultipartFile[] file;
}