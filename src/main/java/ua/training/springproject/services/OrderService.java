package ua.training.springproject.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.*;
import ua.training.springproject.repositories.OrderRepository;
import ua.training.springproject.repositories.OrderStatusRepository;
import ua.training.springproject.repositories.TaxiStatusRepository;
import ua.training.springproject.utils.constants.MyConstants;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Set;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final TaxiStatusRepository taxiStatusRepository;
    private final TaxiService taxiService;
    private final UserService userService;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository,
                        TaxiService taxiService, UserService userService, TaxiStatusRepository taxiStatusRepository) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.taxiService = taxiService;
        this.userService = userService;
        this.taxiStatusRepository = taxiStatusRepository;
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

    @Transactional
    public void saveOrder(Order order) {
        order.setDate(Date.valueOf(LocalDate.now()));
        orderRepository.save(order);
        order.getTaxi().forEach(t -> taxiService.updateTaxiStatus(t, t.getTaxiStatus()));
    }

    public Page<Order> getPaginatedOrders(int pageNumber, String sort, String direction){
        Pageable pageable;
        if (direction.equals("desc")){
            pageable = PageRequest.of(pageNumber, MyConstants.PAGE_SIZE, Sort.by(sort).descending());
        }else{
            pageable = PageRequest.of(pageNumber, MyConstants.PAGE_SIZE, Sort.by(sort).ascending());
        }
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public void processOrder(Long orderId, Long userId, boolean delete){
        Order order = orderRepository.getById(orderId);
        order.getTaxi().forEach(t -> taxiService.updateTaxiStatus(t, taxiStatusRepository.findByName("AVAILABLE").orElseThrow(IllegalArgumentException::new)));
        if(delete){
            order.setOrderStatus(orderStatusRepository.findByName("CANCELED").orElseThrow(IllegalArgumentException::new));
            userService.updateUserBalance(userId, order.getTotal().subtract(BigDecimal.valueOf(MyConstants.INITIAL_PRICE)));
        }else {
            order.setOrderStatus(orderStatusRepository.findByName("DONE").orElseThrow(IllegalArgumentException::new));
            userService.increaseDiscount(userId);
        }
        orderRepository.save(order);
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
