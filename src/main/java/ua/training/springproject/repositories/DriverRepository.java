package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.Driver;

public interface DriverRepository extends JpaRepository<Driver, Long> {

}
