package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.TaxiClass;
import ua.training.springproject.entities.TaxiStatus;
import ua.training.springproject.exceptions.TaxiBusyException;
import ua.training.springproject.repositories.TaxiClassRepository;
import ua.training.springproject.repositories.TaxiRepository;

import java.util.*;

@Service
public class TaxiService {

    private final TaxiRepository taxiRepository;

    private final TaxiClassRepository taxiClassRepository;

    @Autowired
    public TaxiService(TaxiRepository taxiRepository, TaxiClassRepository taxiClassRepository) {
        this.taxiRepository = taxiRepository;
        this.taxiClassRepository = taxiClassRepository;
    }

    @Transactional
    public boolean updateTaxiStatus(Taxi taxiToUpdate, TaxiStatus taxiStatus, boolean checkOnAvailability) {
        Taxi taxi = taxiRepository.findById(taxiToUpdate.getId()).orElseThrow(IllegalArgumentException::new);
        if (checkOnAvailability && !taxi.getTaxiStatus().getName().equals("AVAILABLE")) {
            throw new TaxiBusyException("Taxi is busy");
        }
        taxi.setTaxiStatus(taxiStatus);
        taxiRepository.save(taxi);
        return true;
    }

    @Transactional
    public Optional<Taxi> findSuitableCar(OrderDTO orderDTO) {
        TaxiClass taxiClass = taxiClassRepository.findByName(orderDTO.getTaxiClass()).orElseThrow(IllegalArgumentException::new);
        return taxiRepository.findFirstByTaxiStatusAndTaxiClassAndCapacityGreaterThanEqualOrderByCapacityAsc(orderDTO.getTaxiStatus(),
                taxiClass, orderDTO.getPeopleAmount());
    }


    public Set<Taxi> findTaxiOfAnotherClass(OrderDTO orderDTO) {
        return taxiRepository.findByTaxiStatusAndCapacityGreaterThanEqualOrderByTaxiClass(orderDTO.getTaxiStatus(), orderDTO.getPeopleAmount());
    }

    @Transactional
    public Set<Taxi> findSeveralTaxi(OrderDTO orderDTO) {
        Set<Taxi> taxiResult = new HashSet<>();
        if (taxiRepository.getSumOfCapacity(orderDTO.getTaxiStatus()) < orderDTO.getPeopleAmount()) {
            return taxiResult;
        }
        List<Taxi> taxiFromDb = new ArrayList<>(taxiRepository.findByTaxiStatusOrderByCapacityDesc(orderDTO.getTaxiStatus()));
        for (int i = 0, realCapacity = 0; realCapacity < orderDTO.getPeopleAmount(); i++) {
            taxiResult.add(taxiFromDb.get(i));
            realCapacity += taxiFromDb.get(i).getCapacity();
        }
        return taxiResult;
    }

    @Transactional
    public boolean saveTaxi(Taxi taxi) {
        try {
            taxiRepository.findByInfo(taxi.getInfo()).orElseThrow(IllegalArgumentException::new);
            return false;
        } catch (IllegalArgumentException exception) {
            taxiRepository.save(taxi);
        }
        return true;
    }
}
