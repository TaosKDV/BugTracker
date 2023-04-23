package ru.hepera.bug.tracker.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.hepera.bug.tracker.form.CardComponent;
import ru.hepera.bug.tracker.form.DefectForm;
import ru.hepera.bug.tracker.form.NewDefectForm;
import ru.hepera.bug.tracker.model.Attachment;
import ru.hepera.bug.tracker.model.Defect;
import ru.hepera.bug.tracker.model.DefectState;
import ru.hepera.bug.tracker.model.User;
import ru.hepera.bug.tracker.repository.DefectRepository;
import ru.hepera.bug.tracker.repository.UserRepository;

@Controller
@Log4j2
public class DefectTrackingController {

  @Autowired
  DefectRepository defectRepository;

  @Autowired
  UserRepository userRepository;

  @GetMapping(value = {"/defect"})
  public String showDefectPage(@RequestParam(value = "id", required = false) Long defectId, Model model) {
    if (defectId == null) {//если параметр пуст
      return "redirect:/all";
    }
    Defect defect;
    try {
      defect = defectRepository.findById(defectId).orElseThrow();//если дефект не найден
    } catch (NoSuchElementException e) {
      return "redirect:/all";
    }

//    User author = userRepository.findById(defect.getAuthorId()).orElseThrow();
//    User executor = userRepository.findById(defect.getExecutorId()).orElseThrow();

    DefectForm defectForm = ru.hepera.bug.tracker.form.DefectForm.builder()
      .defectId(defect.getId())
      .name(defect.getName())
      .state(defect.getState())
      .importance(defect.getImportance())
      .executorId(defect.getExecutorId())
      .authorId(defect.getAuthorId())
      .foundVersion(defect.getFoundVersion())
      .fixVersion(defect.getFixVersion())
      .steps(defect.getSteps())
      .expectedResult(defect.getExpectedResult())
      .actualResult(defect.getActualResult())
      .build();
    //добавляем список пользователей для выбора
    List<User> users = new LinkedList<>();
    userRepository.findAll().forEach(users::add);
    defectForm.setUsers(users);
    model.addAttribute("defectForm", defectForm);
    return "defect";
  }

  @PostMapping(value = {"/edit/defect"})
  public String editDefect(@RequestParam(value = "id", required = false) Long defectId,
    @ModelAttribute("defectForm") DefectForm defectForm) {
    Defect defect = defectRepository.findById(defectId).orElse(new Defect());
    defect.setName(defectForm.getName());
    defect.setState(defectForm.getState());
    defect.setImportance(defectForm.getImportance());
    defect.setExecutorId(defectForm.getExecutorId());
    defect.setAuthorId(defectForm.getAuthorId());
    defect.setFoundVersion(defectForm.getFoundVersion());
    defect.setFixVersion(defectForm.getFixVersion());
    defect.setSteps(defectForm.getSteps());
    defect.setExpectedResult(defectForm.getExpectedResult());
    defect.setActualResult(defectForm.getActualResult());
    defectRepository.save(defect);
    return "redirect:/all";
  }

  @GetMapping(value = {"/new/defect"})
  public String showNewDefectPage(Model model) {
    NewDefectForm newDefectForm = new NewDefectForm();
    //добавляем список пользователей для выбора
    List<User> users = new LinkedList<>();
    userRepository.findAll().forEach(users::add);
    newDefectForm.setUsers(users);
    model.addAttribute("newDefectForm", newDefectForm);
    return "newDefect";
  }

  @PostMapping(value = {"/new/defect"})
  public String saveHeaderDefect(@ModelAttribute("newDefectForm") NewDefectForm newDefectForm) {
    Defect defect = new Defect();
    defect.setName(newDefectForm.getName());
    defect.setState(DefectState.NEW);
    defect.setImportance(newDefectForm.getImportance());
    defect.setExecutorId(newDefectForm.getExecutorId());
    defect.setAuthorId(newDefectForm.getAuthorId());
    defect.setFoundVersion(newDefectForm.getFoundVersion());
    defect.setFixVersion(newDefectForm.getFixVersion());
    defect.setSteps(newDefectForm.getSteps());
    defect.setExpectedResult(newDefectForm.getExpectedResult());
    defect.setActualResult(newDefectForm.getActualResult());
    if (newDefectForm.getFile() != null && newDefectForm.getFile().length > 0) {
      Set<Attachment> attachmentSet = new HashSet<>();
      for (MultipartFile multipartFile : newDefectForm.getFile()) {
        try {
          Attachment attachment = new Attachment();
          log.info("Получили файл вложение - " + multipartFile.getOriginalFilename());
          attachment.setFileName(multipartFile.getOriginalFilename());
          attachment.setFileValue(convertFileToBase64(multipartFile.getInputStream()));
          attachmentSet.add(attachment);
        } catch (IOException e) {
          log.info("При сохранении файла " + multipartFile.getOriginalFilename() + " произошла ошибка!" + e);
        }
      }
      if (attachmentSet.size() > 0) {
        log.info("Добавляем дефекту вложения - " + attachmentSet);
        defect.setAttachments(attachmentSet);
      }
    }
    log.info("Сохраняем дефект - " + defect);
    defectRepository.save(defect);

    return "redirect:/all";
  }

  private String convertFileToBase64(InputStream fileStream) throws IOException {
    byte[] buffer = new byte[fileStream.available()];
    fileStream.read(buffer);
    return Base64.getEncoder().encodeToString(buffer);
  }

  private File convertBase64ToFile(String fileName, String base64Value) throws IOException {
    byte[] buffer = Base64.getDecoder().decode(base64Value);
    File targetFile = new File(fileName);
    try (OutputStream outStream = new FileOutputStream(targetFile)) {
      outStream.write(buffer);
    }
    return targetFile;
  }

  @GetMapping("/all")
  public String showAll(Model model) {
    List<CardComponent> componentList = new LinkedList<>();
    defectRepository.findAll().forEach(defect -> {
      CardComponent component = CardComponent.builder()
        .executor(userRepository.findById(defect.getExecutorId()).orElseThrow().getUsername())
        .author(userRepository.findById(defect.getAuthorId()).orElseThrow().getUsername())
        .id(defect.getId())
        .name(defect.getName())
        .importance(defect.getImportance())
        .state(defect.getState())
        .build();
      componentList.add(component);
    });
    model.addAttribute("defectListForm", componentList);
    return "defectList";
  }
}
