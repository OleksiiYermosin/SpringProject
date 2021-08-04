package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.TaxiClass;
import ua.training.springproject.repositories.TaxiClassRepository;
import ua.training.springproject.repositories.TaxiRepository;

import java.util.Optional;

@Service
public class TaxiService {

    private final TaxiRepository taxiRepository;

    private final TaxiClassRepository taxiClassRepository;

    @Autowired
    public TaxiService(TaxiRepository taxiRepository, TaxiClassRepository taxiClassRepository) {
        this.taxiRepository = taxiRepository;
        this.taxiClassRepository = taxiClassRepository;
    }

    public void updateTaxiStatus(Taxi taxiToUpdate) {
        Taxi taxi = taxiRepository.findById(taxiToUpdate.getId()).orElseThrow(IllegalArgumentException::new);
        taxi.setTaxiStatus(taxiToUpdate.getTaxiStatus());
        taxiRepository.save(taxi);
    }

    public Optional<Taxi> findSuitableCar(OrderDTO orderDTO) {
        TaxiClass taxiClass = taxiClassRepository.findByName(orderDTO.getTaxiClass()).orElseThrow(IllegalArgumentException::new);
        return taxiRepository.findFirstByTaxiStatusAndTaxiClassAndCapacityGreaterThanEqualOrderByCapacityAsc(orderDTO.getTaxiStatus(),
                taxiClass, orderDTO.getPeopleAmount());
    }
}
