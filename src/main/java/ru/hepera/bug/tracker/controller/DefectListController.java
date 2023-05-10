package ru.hepera.bug.tracker.controller;

import java.util.LinkedList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.hepera.bug.tracker.form.CardComponent;
import ru.hepera.bug.tracker.repository.DefectRepository;
import ru.hepera.bug.tracker.repository.UserRepository;
import ru.hepera.bug.tracker.util.FileUtil;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@Controller
@Log4j2
public class DefectListController {

  @Autowired
  DefectRepository defectRepository;

  @Autowired
  UserRepository userRepository;

  @GetMapping("/all")
  public String showAll(Model model) {
    List<CardComponent> componentList = new LinkedList<>();
    defectRepository.findAll().forEach(defect -> {
      CardComponent component = CardComponent.builder()
        .executor(userRepository.findById(defect.getExecutor().getId()).orElseThrow().getUsername())
        .author(userRepository.findById(defect.getAuthor().getId()).orElseThrow().getUsername())
        .id(defect.getId())
        .name(defect.getName())
        .importance(defect.getImportance())
        .state(defect.getState())
        .build();
      componentList.add(component);
    });
    model.addAttribute("defectListForm", componentList);
    return "defectList.html";
  }

  @GetMapping("/download/csv")
  @ResponseBody
  public ResponseEntity<InputStreamResource> downloadCsv() {
    return ResponseEntity.ok()
      .header(CONTENT_DISPOSITION, "filename*=UTF-8''report.csv")
      .contentType(MediaType.TEXT_PLAIN)
      .body(new InputStreamResource(FileUtil.getCsv(defectRepository.findAll())));
  }

  @GetMapping("/download/xlsx")
  @ResponseBody
  public ResponseEntity<InputStreamResource> downloadXlsx() {
    return ResponseEntity.ok()
      .header(CONTENT_DISPOSITION, "filename*=UTF-8''report.xlsx")
      .contentType(MediaType.TEXT_PLAIN)
      .body(new InputStreamResource(FileUtil.getXlsx(defectRepository.findAll())));
  }
}
