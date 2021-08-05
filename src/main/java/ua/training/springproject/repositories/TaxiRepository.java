package ua.training.springproject.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.TaxiClass;
import ua.training.springproject.entities.TaxiStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface TaxiRepository extends JpaRepository<Taxi, Long> {

    Optional<Taxi> findFirstByTaxiStatusAndTaxiClassAndCapacityGreaterThanEqualOrderByCapacityAsc(TaxiStatus taxiStatus,
                                                                                                  TaxiClass taxiClass,
                                                                                                  int capacity);

    Set<Taxi> findByTaxiStatusAndCapacityGreaterThanEqualOrderByTaxiClass(TaxiStatus taxiStatus,
                                                                          int capacity);

    Set<Taxi> findByTaxiStatusOrderByCapacityDesc(TaxiStatus taxiStatus);

    @Query("SELECT SUM(t.capacity) FROM Taxi t WHERE t.taxiStatus=?1")
    Integer getSumOfCapacity(TaxiStatus taxiStatus);
}
