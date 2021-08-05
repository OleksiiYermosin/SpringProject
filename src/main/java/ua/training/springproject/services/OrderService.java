package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.*;
import ua.training.springproject.repositories.OrderRepository;
import ua.training.springproject.repositories.OrderStatusRepository;
import ua.training.springproject.utils.constants.MyConstants;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final TaxiService taxiService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, TaxiService taxiService) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.taxiService = taxiService;
    }

    public Order prepareOrder(OrderDTO orderDTO, Set<Taxi> taxi, User user) {
        BigDecimal distance = calculateTimeOrDistance(orderDTO.getStartAddress() + orderDTO.getFinishAddress(), MyConstants.AVERAGE_DISTANCE, 2);
        BigDecimal time = calculateTimeOrDistance(orderDTO.getStartAddress(), MyConstants.AVERAGE_TIME, 0);
        BigDecimal multiplier = taxi.stream().map(t -> t.getTaxiClass().getMultiplier()).reduce(BigDecimal.valueOf(0), BigDecimal::add);
        BigDecimal total = calculateTotal(multiplier.doubleValue(), user.getDiscount().doubleValue(), distance.doubleValue());
        taxi.forEach(t -> t.setTaxiStatus(new TaxiStatus(2L, "BUSY")));
        return Order.builder()
                .user(user)
                .orderStatus(new OrderStatus(1L, "ACTIVE"))
                .total(total)
                .addressFrom(orderDTO.getStartAddress())
                .addressTo(orderDTO.getFinishAddress())
                .distance(distance)
                .time(time)
                .peopleAmount(orderDTO.getPeopleAmount())
                .taxi(taxi)
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOrder(Order order) {
        orderRepository.save(order);
        order.getTaxi().forEach(taxiService::updateTaxiStatus);
    }

    private BigDecimal calculateTimeOrDistance(String hashString, double multiplier, int scale) {
        double value = (Math.cos(hashString.hashCode()) + 1.5) * multiplier;
        return BigDecimal.valueOf(value).setScale(scale, BigDecimal.ROUND_UP);
    }


    private BigDecimal calculateTotal(double multiplier, double discount, double distance) {
        double value = multiplier * ((distance * MyConstants.ADDITIONAL_PRICE) + MyConstants.INITIAL_PRICE);
        return BigDecimal.valueOf(value - (value * discount / 100)).setScale(2, BigDecimal.ROUND_UP);
    }

}
