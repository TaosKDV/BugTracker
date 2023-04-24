package ru.hepera.bug.tracker.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import ru.hepera.bug.tracker.form.DefectForm;
import ru.hepera.bug.tracker.form.NewDefectForm;
import ru.hepera.bug.tracker.model.Attachment;
import ru.hepera.bug.tracker.model.Defect;
import ru.hepera.bug.tracker.model.DefectState;
import ru.hepera.bug.tracker.model.User;
import ru.hepera.bug.tracker.repository.DefectRepository;
import ru.hepera.bug.tracker.repository.UserRepository;
import ru.hepera.bug.tracker.util.FileUtil;

@Controller
@Log4j2
public class DefectTrackingController {

  @Autowired
  DefectRepository defectRepository;

  @Autowired
  UserRepository userRepository;

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
    User author = userRepository.findById(newDefectForm.getAuthorId()).orElseThrow();
    User executor = userRepository.findById(newDefectForm.getExecutorId()).orElseThrow();
    Defect defect = new Defect();
    defect.setName(newDefectForm.getName());
    defect.setState(DefectState.NEW);
    defect.setImportance(newDefectForm.getImportance());
    defect.setExecutor(executor);
    defect.setAuthor(author);
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
          attachment.setFileValue(FileUtil.convertFileToBase64(multipartFile.getInputStream()));
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
    DefectForm defectForm = ru.hepera.bug.tracker.form.DefectForm.builder()
      .defectId(defect.getId())
      .name(defect.getName())
      .state(defect.getState())
      .importance(defect.getImportance())
      .executorId(defect.getExecutor().getId())
      .authorId(defect.getAuthor().getId())
      .foundVersion(defect.getFoundVersion())
      .fixVersion(defect.getFixVersion())
      .steps(defect.getSteps())
      .expectedResult(defect.getExpectedResult())
      .actualResult(defect.getActualResult())
      .attachmentNames(defect.getAttachments().stream().map(Attachment::getFileName).toArray(String[]::new))
      .build();
    //добавляем список пользователей для выбора
    List<User> users = new LinkedList<>();
    userRepository.findAll().forEach(users::add);
    defectForm.setUsers(users);
    log.info("defectForm - " + defectForm);
    model.addAttribute("defectForm", defectForm);
    return "defect";
  }

  @PostMapping(value = {"/edit/defect"})
  public String editDefect(@RequestParam(value = "id", required = false) Long defectId,
    @ModelAttribute("defectForm") DefectForm defectForm) {
    User author = userRepository.findById(defectForm.getAuthorId()).orElseThrow();
    User executor = userRepository.findById(defectForm.getExecutorId()).orElseThrow();
    Defect defect = defectRepository.findById(defectId).orElse(new Defect());
    defect.setName(defectForm.getName());
    defect.setState(defectForm.getState());
    defect.setImportance(defectForm.getImportance());
    defect.setExecutor(executor);
    defect.setAuthor(author);
    defect.setFoundVersion(defectForm.getFoundVersion());
    defect.setFixVersion(defectForm.getFixVersion());
    defect.setSteps(defectForm.getSteps());
    defect.setExpectedResult(defectForm.getExpectedResult());
    defect.setActualResult(defectForm.getActualResult());
    if (defectForm.getFile() != null && defectForm.getFile().length > 0) {
      Set<Attachment> attachmentSet = new HashSet<>();
      for (MultipartFile multipartFile : defectForm.getFile()) {
        try {
          Attachment attachment = new Attachment();
          log.info("Получили файл вложение - " + multipartFile.getOriginalFilename());
          attachment.setFileName(multipartFile.getOriginalFilename());
          attachment.setFileValue(FileUtil.convertFileToBase64(multipartFile.getInputStream()));
          attachmentSet.add(attachment);
        } catch (IOException e) {
          log.info("При сохранении файла " + multipartFile.getOriginalFilename() + " произошла ошибка!" + e);
        }
      }
      if (attachmentSet.size() > 0) {
        log.info("Добавляем дефекту вложения - " + attachmentSet);
        attachmentSet.addAll(Optional.ofNullable(defect.getAttachments()).orElse(new HashSet<>()));
        defect.setAttachments(attachmentSet);
      }
    }
    defectRepository.save(defect);
    return "redirect:/all";
  }

  @GetMapping(value = {"/attachment/defect"})
  @ResponseBody
  public ResponseEntity<InputStreamResource> showAttachment(
    @RequestParam(value = "id", required = false) Long defectId,
    @RequestParam(value = "name", required = false) String name) throws IOException {
    Defect defect = defectRepository.findById(defectId).orElse(new Defect());
    Attachment attachment = defect.getAttachments().stream()
      .filter(a -> a.getFileName().equals(name)).findFirst().orElseThrow();
    InputStream in = new FileInputStream(FileUtil.convertBase64ToFile(attachment.getFileName(), attachment.getFileValue()));
    String fileFormat = StringUtils.substringAfter(name, ".");
    MediaType contentType = switch (fileFormat) {
      case "jpg", "jpeg" -> MediaType.IMAGE_JPEG;
      case "png" -> MediaType.IMAGE_PNG;
      case "json" -> MediaType.APPLICATION_JSON;
      case "pdf" -> MediaType.APPLICATION_PDF;
      case "xml" -> MediaType.TEXT_XML;
      case "gif" -> MediaType.IMAGE_GIF;
      case "html" -> MediaType.TEXT_HTML;
      default -> MediaType.TEXT_PLAIN;
    };
    return ResponseEntity.ok().contentType(contentType).body(new InputStreamResource(in));
  }
}
