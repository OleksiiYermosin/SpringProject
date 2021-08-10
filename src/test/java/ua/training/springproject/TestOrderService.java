package ua.training.springproject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.entities.*;
import ua.training.springproject.repositories.OrderRepository;
import ua.training.springproject.services.OrderService;
import ua.training.springproject.services.TaxiService;
import ua.training.springproject.services.UserService;

import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SpringProjectApplication.class)
public class TestOrderService {

    private TaxiService taxiService;
    private UserService userService;
    private OrderService orderService;
    private OrderRepository orderRepository;

    @Autowired
    public void setTaxiService(TaxiService taxiService) {
        this.taxiService = taxiService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository){
        this.orderRepository = orderRepository;
    }

    @Test
    public void testOrderPrepare(){
        OrderDTO orderDTO = prepareOrderDTO("BUSINESS");
        Taxi taxi = getTaxi(orderDTO);
        User user = getUser();
        Assert.assertNotNull(orderService.prepareOrder(orderDTO, Collections.singleton(taxi), user));
    }

    @Test
    public void testOrderSave(){
        OrderDTO orderDTO = prepareOrderDTO("ECONOMY");
        Taxi taxi = getTaxi(orderDTO);
        User user = getUser();
        Assert.assertNotNull(orderService.saveOrder(orderService.prepareOrder(orderDTO, Collections.singleton(taxi), user)));
    }

    @Test
    public void testOrderProcess(){
        OrderDTO orderDTO = prepareOrderDTO("BUSINESS");
        Taxi taxi = getTaxi(orderDTO);
        User user = getUser();
        Long id = orderService.saveOrder(orderService.prepareOrder(orderDTO, Collections.singleton(taxi), user));
        Assert.assertNotNull(orderService.processOrder(id, true));
    }

    private OrderDTO prepareOrderDTO(String taxiClass){
        return OrderDTO.builder().startAddress("Start").finishAddress("Finish").peopleAmount(1).taxiStatus(new TaxiStatus(1L, "AVAILABLE"))
                .taxiClass(taxiClass).build();
    }

    private Taxi getTaxi(OrderDTO orderDTO){
        return taxiService.findSuitableCar(orderDTO).orElseThrow(IllegalArgumentException::new);
    }

    private User getUser(){
        return (User) userService.loadUserByUsername("nick");
    }


}
