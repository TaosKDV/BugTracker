package ru.hepera.bug.tracker.repository;


import org.springframework.data.repository.CrudRepository;
import ru.hepera.bug.tracker.model.Defect;

public interface DefectRepository extends CrudRepository<Defect, Long> {

}
