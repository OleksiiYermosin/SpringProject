package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.TaxiStatus;

import java.util.Optional;

public interface TaxiStatusRepository extends JpaRepository<TaxiStatus, Long> {

    Optional<TaxiStatus> findByName(String name);

}
