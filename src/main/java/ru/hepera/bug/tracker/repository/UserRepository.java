package ru.hepera.bug.tracker.repository;


import org.springframework.data.repository.CrudRepository;
import ru.hepera.bug.tracker.model.User;

public interface UserRepository extends CrudRepository<User, Long> {

  User findByUsername(String username);
}
