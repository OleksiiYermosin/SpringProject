package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.TaxiClass;
import ua.training.springproject.entities.TaxiStatus;
import ua.training.springproject.repositories.TaxiClassRepository;
import ua.training.springproject.repositories.TaxiRepository;
import ua.training.springproject.repositories.TaxiStatusRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TaxiService {

    private final TaxiRepository taxiRepository;

    private final TaxiClassRepository taxiClassRepository;

    private final TaxiStatusRepository taxiStatusRepository;

    @Autowired
    public TaxiService(TaxiRepository taxiRepository, TaxiClassRepository taxiClassRepository, TaxiStatusRepository taxiStatusRepository) {
        this.taxiRepository = taxiRepository;
        this.taxiClassRepository = taxiClassRepository;
        this.taxiStatusRepository = taxiStatusRepository;
    }

    public Optional<Taxi> findSuitableCar(OrderDTO orderDTO){
        TaxiStatus taxiStatus = new TaxiStatus(1L, "AVAILABLE");
        TaxiClass taxiClass = taxiClassRepository.findByName(orderDTO.getTaxiClass()).orElseThrow(IllegalArgumentException::new);
        return taxiRepository.findFirstByTaxiStatusAndTaxiClassAndCapacityGreaterThanEqualOrderByCapacityAsc(taxiStatus,
                taxiClass, orderDTO.getPeopleAmount());
    }

    /*public Taxi findTaxiByStatusAndClass(TaxiStatus taxiStatus, TaxiClass taxiClass){
        Optional<Taxi> optionalTaxi = taxiRepository.findByTaxiStatusAndTaxiClass(taxiStatus, taxiClass);
        return Optional.ofNullable(optionalTaxi).get().orElseThrow(IllegalArgumentException::new);
    }*/
}
