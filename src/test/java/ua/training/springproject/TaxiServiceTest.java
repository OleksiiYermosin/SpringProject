package ua.training.springproject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.*;
import ua.training.springproject.repositories.DriverRepository;
import ua.training.springproject.repositories.TaxiClassRepository;
import ua.training.springproject.repositories.TaxiRepository;
import ua.training.springproject.repositories.TaxiStatusRepository;
import ua.training.springproject.services.TaxiService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringProjectApplication.class)
public class TaxiServiceTest {

    private TaxiService taxiService;
    private DriverRepository driverRepository;
    private TaxiClassRepository taxiClassRepository;
    private TaxiStatusRepository taxiStatusRepository;
    private TaxiRepository taxiRepository;
    private Taxi taxi;

    @Autowired
    public void setTaxiService(TaxiService taxiService) {
        this.taxiService = taxiService;
    }

    @Autowired
    public void setDriverRepository(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Autowired
    public void setTaxiClassRepository(TaxiClassRepository taxiClassRepository) {
        this.taxiClassRepository = taxiClassRepository;
    }

    @Autowired
    public void setTaxiStatusRepository(TaxiStatusRepository taxiStatusRepository) {
        this.taxiStatusRepository = taxiStatusRepository;
    }

    @Autowired
    public void setTaxiRepository(TaxiRepository taxiRepository) {
        this.taxiRepository = taxiRepository;
    }

    @Test
    public void testStatusUpdate() {
        taxi = taxiRepository.getById(1L);
        boolean result = taxiService.updateTaxiStatus(taxi, taxiStatusRepository.findByName("BUSY").orElseThrow(IllegalArgumentException::new), false);
        taxiService.updateTaxiStatus(taxi, taxiStatusRepository.findByName("AVAILABLE").orElseThrow(IllegalArgumentException::new), false);
        Assert.assertTrue(result);
    }

    @Test
    public void testSuitableCarSearch() {
        OrderDTO orderDTO = OrderDTO.builder().taxiClass("ECONOMY").peopleAmount(1).taxiStatus(taxiStatusRepository.getById(1L)).build();
        if (!taxiService.findSuitableCar(orderDTO).isPresent()) {
            Assert.fail();
        }
    }

    @Test
    public void testNotSuitableCarSearch() {
        OrderDTO orderDTO = OrderDTO.builder().taxiClass("ECONOMY").peopleAmount(3).build();
        if (taxiService.findSuitableCar(orderDTO).isPresent()) {
            Assert.fail();
        }
    }

    @Test
    public void testTaxiOfOtherClassesSearch() {
        Taxi taxiOfOtherClass = Taxi.builder().info("Black Mercedes; Numbers: XX0000XX").taxiClass(taxiClassRepository.getById(2L)).capacity(3)
                .taxiStatus(taxiStatusRepository.getById(1L)).driver(driverRepository.getById(2L)).build();
        taxiService.saveTaxi(taxiOfOtherClass);
        if (taxiService.findTaxiOfAnotherClass(OrderDTO.builder().taxiClass("ECONOMY").peopleAmount(3).taxiStatus(taxiStatusRepository.getById(1L)).build()).isEmpty()) {
            Assert.fail();
        }
    }

    @Test
    public void testSeveralTaxiSearch() {
        Taxi taxiOfOtherClass = Taxi.builder().info("Black Mercedes; Numbers: XX0000XX").taxiClass(taxiClassRepository.getById(1L)).capacity(3)
                .taxiStatus(taxiStatusRepository.getById(1L)).driver(driverRepository.getById(2L)).build();
        taxiService.saveTaxi(taxiOfOtherClass);
        if (taxiService.findSeveralTaxi(OrderDTO.builder().taxiClass("ECONOMY").peopleAmount(4).taxiStatus(taxiStatusRepository.getById(1L)).build()).isEmpty()) {
            Assert.fail();
        }
    }

    @Test
    public void testTaxiSave() {
        taxi = Taxi.builder().info("Black BMW; Numbers: AA1111AA").taxiClass(taxiClassRepository.getById(1L)).taxiStatus(taxiStatusRepository.getById(1L))
                .capacity(1).driver(driverRepository.getById(1L)).build();
        Assert.assertFalse(taxiService.saveTaxi(taxi));
    }

}
