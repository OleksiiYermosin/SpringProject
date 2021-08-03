package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.TaxiClass;
import ua.training.springproject.entities.TaxiStatus;

import java.util.List;
import java.util.Optional;

public interface TaxiRepository extends JpaRepository<Taxi, Long> {

    Optional<Taxi> findFirstByTaxiStatusAndTaxiClassAndCapacityGreaterThanEqualOrderByCapacityAsc(TaxiStatus taxiStatus,
                                                                                                  TaxiClass taxiClass,
                                                                                                  int capacity);

}
