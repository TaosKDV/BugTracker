package ru.hepera.bug.tracker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.hepera.bug.tracker.form.NewDefectForm;
import ru.hepera.bug.tracker.model.Defect;
import ru.hepera.bug.tracker.model.DefectState;
import ru.hepera.bug.tracker.repository.DefectRepository;

@Controller
public class DefectTrackingController {

  @Autowired
  DefectRepository defectRepository;

  @RequestMapping({"/new/defect"})
  public String newDefect() {
    return "newDefect";
  }

  @RequestMapping(value = {"/new/defect"}, method = RequestMethod.GET)
  public String showNewDefectPage(Model model) {

    NewDefectForm newDefectForm = new NewDefectForm();
    model.addAttribute("newDefectForm", newDefectForm);

    return "newDefect";
  }

  @RequestMapping(value = {"/new/defect"}, method = RequestMethod.POST)
  public String saveHeaderNewDefect(@ModelAttribute("newDefectForm") NewDefectForm newDefectForm) {
    Defect defect = Defect.builder()
        .name(newDefectForm.getName())
        .state(DefectState.NEW)
        .importance(newDefectForm.getImportance())
        .executorId(newDefectForm.getExecutorId())
        .authorId(newDefectForm.getAuthor().getId())
        .foundVersion(newDefectForm.getFoundVersion())
        .fixVersion(newDefectForm.getFixVersion())
        .build();

    defectRepository.save(defect);
    return null;
  }

}
