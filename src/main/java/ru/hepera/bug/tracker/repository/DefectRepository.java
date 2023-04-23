package ru.hepera.bug.tracker.repository;


import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.hepera.bug.tracker.model.Defect;

public interface DefectRepository extends CrudRepository<Defect, Long> {

  @Query(value = "SELECT * FROM DEFECTS WHERE STATE = ?1",
    nativeQuery = true)
  List<Defect> findAllIdByState(String state);

}
