package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
