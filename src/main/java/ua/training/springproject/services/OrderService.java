package ua.training.springproject.services;

import com.querydsl.core.types.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.dto.PageInfoDTO;
import ua.training.springproject.entities.*;
import ua.training.springproject.exceptions.StatusNotFoundException;
import ua.training.springproject.repositories.OrderRepository;
import ua.training.springproject.repositories.OrderStatusRepository;
import ua.training.springproject.repositories.TaxiStatusRepository;
import ua.training.springproject.utils.FilterPredicate;
import ua.training.springproject.utils.constants.MyConstants;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

/**
 * Service for processing orders
 */
@Service
public class OrderService {

    /**
     * Order`s repository
     */
    private final OrderRepository orderRepository;

    /**
     * Order`s status repository
     */
    private final OrderStatusRepository orderStatusRepository;

    /**
     * Taxi`s status repository
     */
    private final TaxiStatusRepository taxiStatusRepository;

    /**
     * Taxi`s service
     */
    private final TaxiService taxiService;

    /**
     * User`s service
     */
    private final UserService userService;

    /**
     * Calculation`s service
     */
    private final CalculationService calculationService;

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(OrderService.class);

    /**
     * Constructor for dependency injection
     * @param orderRepository - order`s repository bean
     * @param orderStatusRepository - order`s status repository bean
     * @param taxiService - taxi`s service bean
     * @param userService - user`s service bean
     * @param taxiStatusRepository - taxi`s status repository bean
     * @param calculationService - calculation`s service bean
     */
    @Autowired
    public OrderService(OrderRepository orderRepository, OrderStatusRepository orderStatusRepository, TaxiService taxiService,
                        UserService userService, TaxiStatusRepository taxiStatusRepository, CalculationService calculationService) {
        this.orderRepository = orderRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.taxiService = taxiService;
        this.userService = userService;
        this.taxiStatusRepository = taxiStatusRepository;
        this.calculationService = calculationService;
    }

    /**
     * Method for assembling order
     * @param orderDTO - order`s DTO
     * @param taxi - set of taxi
     * @param user - user
     * @return prepared order
     */
    public Order prepareOrder(OrderDTO orderDTO, Set<Taxi> taxi, User user) {
        logger.info("Preparing order...");
        BigDecimal distance = calculationService.calculateTimeOrDistance(orderDTO.getStartAddress() + orderDTO.getFinishAddress(), MyConstants.AVERAGE_DISTANCE, 2);
        BigDecimal multiplier = taxi.stream().map(t -> t.getTaxiClass().getMultiplier()).reduce(BigDecimal.valueOf(0), BigDecimal::add);
        TaxiStatus taxiStatus = taxiStatusRepository.findByName("BUSY").orElseThrow(StatusNotFoundException::new);
        taxi.forEach(t -> t.setTaxiStatus(taxiStatus));
        return Order.builder()
                .user(user)
                .orderStatus(new OrderStatus(1L, "ACTIVE"))
                .total(calculationService.calculateTotal(multiplier.doubleValue(), user.getDiscount().doubleValue(), distance.doubleValue()))
                .addressFrom(orderDTO.getStartAddress())
                .addressTo(orderDTO.getFinishAddress())
                .distance(distance)
                .time(calculationService.calculateTimeOrDistance(orderDTO.getStartAddress(), MyConstants.AVERAGE_TIME, 0))
                .peopleAmount(orderDTO.getPeopleAmount())
                .taxi(taxi)
                .build();
    }

    /**
     * Method for saving order
     * @param order - order`s object
     * @return id of saved object
     */
    @Transactional
    public Long saveOrder(Order order) {
        order.setDate(LocalDate.now());
        order.getTaxi().forEach(t -> taxiService.updateTaxiStatus(t, t.getTaxiStatus(), true));
        userService.getMoneyFromUser(order.getTotal(), order.getUser().getId());
        return orderRepository.save(order).getId();
    }

    /**
     * Method for getting orders with sorting and filtering
     * @param pageInfoDTO - page info DTO
     * @param predicate - predicate
     * @return page of orders
     */
    public Page<Order> getPaginatedOrders(PageInfoDTO pageInfoDTO, Predicate predicate) {
        logger.info("Getting pages with sort and filter");
        Pageable pageable = PageRequest.of(pageInfoDTO.getPage(), MyConstants.PAGE_SIZE,
                pageInfoDTO.getSortDirection().equals("desc") ? Sort.by(pageInfoDTO.getSort()).descending() : Sort.by(pageInfoDTO.getSort()).ascending());
        return (predicate == null ? orderRepository.findAll(pageable) : orderRepository.findAll(predicate, pageable));
    }

    /**
     * Method for getting orders with sorting
     * @param page - page to be loaded
     * @param sort - column by which orders will be sorted
     * @param sortDirection = sort direction
     * @param user - user
     * @return page of orders
     */
    public Page<Order> getPaginatedOrdersByUser(Integer page, String sort, String sortDirection, User user) {
        logger.info("Getting sorted pages");
        Pageable pageable = PageRequest.of(page, MyConstants.PAGE_SIZE,
                sortDirection.equals("desc") ? Sort.by(sort).descending() : Sort.by(sort).ascending());
        return orderRepository.findAllByUser(user, pageable);
    }

    /**
     * Method for cancelling or finishing order
     * @param orderId - order`s id
     * @param delete - is order be deleted
     * @return id of order
     */
    @Transactional
    public Long processOrder(Long orderId, boolean delete) {
        Order order = orderRepository.getById(orderId);
        order.getTaxi().forEach(t -> taxiService.updateTaxiStatus(t, taxiStatusRepository.findByName("AVAILABLE").orElseThrow(StatusNotFoundException::new), false));
        if (delete) {
            order.setOrderStatus(orderStatusRepository.findByName("CANCELED").orElseThrow(StatusNotFoundException::new));
            userService.updateUserBalance(order.getUser().getId(), order.getTotal().subtract(BigDecimal.valueOf(MyConstants.INITIAL_PRICE)));
        } else {
            order.setOrderStatus(orderStatusRepository.findByName("DONE").orElseThrow(StatusNotFoundException::new));
            userService.increaseDiscount(order.getUser().getId());
        }
        return orderRepository.save(order).getId();
    }

    /**
     * Method for creating predicate for filtering
     * @param pageInfoDTO - page infoDTO
     * @return predicate
     */
    public Predicate makePredicate(PageInfoDTO pageInfoDTO) {
        logger.info("Creating predicate");
        return FilterPredicate.builder().add(pageInfoDTO.isSearchByName() && !pageInfoDTO.getName().isEmpty() ? pageInfoDTO.getName() : null, QOrder.order.user.name::eq)
                .add(pageInfoDTO.isSearchByName() && !pageInfoDTO.getSurname().isEmpty() ? pageInfoDTO.getSurname() : null, QOrder.order.user.surname::eq)
                .add(pageInfoDTO.isSearchByDate() ? LocalDate.parse(pageInfoDTO.getDate()) : null, QOrder.order.date::eq).build();
    }


}
