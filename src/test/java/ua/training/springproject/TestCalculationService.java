package ua.training.springproject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.springproject.services.CalculationService;
import ua.training.springproject.utils.constants.MyConstants;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringProjectApplication.class)
public class TestCalculationService {

    private CalculationService calculationService;

    @Autowired
    public void setCalculationService(CalculationService calculationService) {
        this.calculationService = calculationService;
    }

    @Test
    public void testTimeCalculator(){
        String string = "Test";
        double multiplier = 7.0;
        int scale = 2;
        double value = (Math.cos(string.hashCode()) + 1.5) * multiplier;
        BigDecimal result = BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_UP);
        Assert.assertEquals(0, result.compareTo(calculationService.calculateTimeOrDistance(string, multiplier, scale)));
    }

    @Test
    public void testTotalCalculator(){
        String string = "Test";
        int scale = 2;
        double discount = 0.0;
        double multiplier = 7.0, distance = calculationService.calculateTimeOrDistance(string, multiplier, scale).doubleValue();
        double value = multiplier * ((distance * MyConstants.ADDITIONAL_PRICE) + MyConstants.INITIAL_PRICE);
        BigDecimal result = BigDecimal.valueOf(value - (value * discount / 100)).setScale(2, BigDecimal.ROUND_UP);
        Assert.assertEquals(0, result.compareTo(calculationService.calculateTotal(multiplier, discount, distance)));
    }

}
