package ua.training.springproject.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import ua.training.springproject.utils.constants.MyConstants;

import java.math.BigDecimal;

/**
 * Service for calculating distance, time and order`s total
 */
@Service
public class CalculationService {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(CalculationService.class);

    /**
     * Method for calculating time or distance
     * @param hashString - address
     * @param multiplier - average time or distance multiplier
     * @param scale - scale
     * @return calculated time or distance
     */
    public BigDecimal calculateTimeOrDistance(String hashString, double multiplier, int scale) {
        logger.info("Calculating time or distance");
        double value = (Math.cos(hashString.hashCode()) + 1.5) * multiplier;
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_UP);
    }

    /**
     * Method for calculating total
     * @param multiplier - taxi`s class multiplier
     * @param discount - user`s discount
     * @param distance - distance
     * @return calculated order`s total
     */
    public BigDecimal calculateTotal(double multiplier, double discount, double distance) {
        logger.info("Calculating order total");
        double value = multiplier * ((distance * MyConstants.ADDITIONAL_PRICE) + MyConstants.INITIAL_PRICE);
        return BigDecimal.valueOf(value - (value * discount / 100)).setScale(2, BigDecimal.ROUND_UP);
    }

}
