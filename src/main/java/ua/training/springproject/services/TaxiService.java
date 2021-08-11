package ua.training.springproject.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

/**
 * Service for processing taxi
 */
@Service
public class TaxiService {

    /**
     * Taxi`s repository
     */
    private final TaxiRepository taxiRepository;

    /**
     * Taxi`s class repository
     */
    private final TaxiClassRepository taxiClassRepository;

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(TaxiService.class);

    /**
     * Constructor for dependency injection
     * @param taxiRepository - taxi`s repository bean
     * @param taxiClassRepository - taxi`s class repository bean
     */
    @Autowired
    public TaxiService(TaxiRepository taxiRepository, TaxiClassRepository taxiClassRepository) {
        this.taxiRepository = taxiRepository;
        this.taxiClassRepository = taxiClassRepository;
    }

    /**
     * Method for updating taxi`s status
     * @param taxiToUpdate - taxi
     * @param taxiStatus - new status
     * @param checkOnAvailability - is need to check on availability
     * @return return true if status was updated
     */
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

    /**
     * Method for search suitable car
     * @param orderDTO - order DTO
     * @return optional of taxi
     */
    @Transactional
    public Optional<Taxi> findSuitableCar(OrderDTO orderDTO) {
        TaxiClass taxiClass = taxiClassRepository.findByName(orderDTO.getTaxiClass()).orElseThrow(IllegalArgumentException::new);
        return taxiRepository.findFirstByTaxiStatusAndTaxiClassAndCapacityGreaterThanEqualOrderByCapacityAsc(orderDTO.getTaxiStatus(),
                taxiClass, orderDTO.getPeopleAmount());
    }

    /**
     * Method for finding taxis of other classes
     * @param orderDTO - order DTO
     * @return - set of taxi
     */
    public Set<Taxi> findTaxiOfAnotherClass(OrderDTO orderDTO) {
        logger.info("Finding cars by another class");
        return taxiRepository.findByTaxiStatusAndCapacityGreaterThanEqualOrderByTaxiClass(orderDTO.getTaxiStatus(), orderDTO.getPeopleAmount());
    }

    /**
     * Method for finding several taxi
     * @param orderDTO - order DTO
     * @return set of taxi
     */
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

    /**
     * Method for saving new taxi
     * @param taxi - taxi to be saved
     * @return true if taxi was save, otherwise false
     */
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
