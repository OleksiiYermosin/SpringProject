package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.Order;
import ua.training.springproject.entities.OrderStatus;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.User;
import ua.training.springproject.repositories.OrderRepository;

import java.math.BigDecimal;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order prepareOrder(OrderDTO orderDTO, Set<Taxi> taxi, User user){
        BigDecimal distance = calculateDistance(orderDTO);
        BigDecimal total = calculateTotal(taxi, user.getDiscount(), distance);
        return Order.builder()
                .user(user)
                .orderStatus(new OrderStatus(1L, "ACTIVE"))
                .total(total)
                .addressFrom(orderDTO.getStartAddress())
                .addressTo(orderDTO.getFinishAddress())
                .distance(distance)
                .peopleAmount(orderDTO.getPeopleAmount())
                .taxi(taxi).build();
    }

    private BigDecimal calculateTotal(Set<Taxi> taxis, BigDecimal discount, BigDecimal distance){
        return new BigDecimal("1");
    }

    private BigDecimal calculateDistance(OrderDTO orderDTO){
        return new BigDecimal("1");
    }

}
