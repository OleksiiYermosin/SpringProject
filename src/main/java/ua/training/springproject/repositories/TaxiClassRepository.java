package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.TaxiClass;

import java.util.Optional;

public interface TaxiClassRepository extends JpaRepository<TaxiClass, Long> {

    Optional<TaxiClass> findByName(String name);

}
