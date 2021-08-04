package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
