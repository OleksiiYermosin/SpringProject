package ua.training.springproject.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.dto.ValueDTO;
import ua.training.springproject.entities.Order;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.User;
import ua.training.springproject.exceptions.NotEnoughMoneyException;
import ua.training.springproject.exceptions.TaxiBusyException;
import ua.training.springproject.exceptions.UserNotFoundException;
import ua.training.springproject.services.OrderService;
import ua.training.springproject.services.TaxiService;
import ua.training.springproject.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.*;


@Controller
@RequestMapping("/user")
public class UserController {

    /**
     * User service object
     */
    private final UserService userService;

    /**
     * Taxi service object
     */
    private final TaxiService taxiService;

    /**
     * Order service object
     */
    private final OrderService orderService;

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(UserController.class);

    /**
     * Constructor for dependency injection
     * @param userService - user service`s bean
     * @param taxiService - taxi service`s bean
     * @param orderService - order service`s bean
     */
    @Autowired
    public UserController(UserService userService, TaxiService taxiService, OrderService orderService) {
        this.userService = userService;
        this.taxiService = taxiService;
        this.orderService = orderService;
    }

    /**
     * Method for retrieving user`s main page
     * @return user`s account view
     */
    @GetMapping("/")
    public String index() {
        return "users/user";
    }

    /**
     * Method for retrieving recharge balance page
     * @param model - model
     * @return recharge balance`s view
     */
    @GetMapping("/recharge-balance")
    public String getRechargePage(Model model) {
        logger.info("User reached recharge page");
        model.addAttribute("valueDTO", new ValueDTO());
        return "users/addmoney";
    }

    /**
     * Method for recharging user`s balance
     * @param valueDTO - value DTO
     * @param bindingResult - binding result
     * @return user`s account view
     */
    @PostMapping("/recharge-balance")
    public String addMoney(@ModelAttribute("valueDTO") @Valid ValueDTO valueDTO, BindingResult bindingResult) {
        logger.info("Trying to add money");
        if (bindingResult.hasErrors()) {
            logger.warn("Value validation failed");
            return "users/addmoney";
        }
        userService.updateUserBalance(getUser().getId(), valueDTO.getValue());
        return "redirect:/user/";
    }

    /**
     * Method for retrieving booking form
     * @param model - model
     * @return booking form`s view
     */
    @GetMapping("/order-taxi")
    public String getOrderPage(Model model) {
        logger.info("User reached booking page");
        model.addAttribute("orderDTO", new OrderDTO());
        return "users/ordertaxi";
    }

    /**
     * Method for creating new prepared order
     * @param orderDTO - order`s DTO
     * @param bindingResult - binding result
     * @param session - HTTTP Session
     * @return prepared order`s view
     */
    @PostMapping("/order-taxi")
    public String getOrders(@ModelAttribute("orderDTO") @Valid OrderDTO orderDTO, BindingResult bindingResult, HttpSession session) {
        logger.info("Finding possible variants...");
        if (bindingResult.hasErrors()) {
            logger.warn("Order validation failed");
            return "users/ordertaxi";
        }
        Set<Taxi> taxis = new HashSet<>();
        User user = getUser();
        boolean isTaxiFound;
        session.setAttribute("isFound", false);
        taxiService.findSuitableCar(orderDTO).ifPresent(taxis::add);
        isTaxiFound = setAttribute(session, taxis, orderDTO, user, false);
        if(!isTaxiFound) {
            logger.info("Suitable taxi wasn`t found");
            taxis = taxiService.findTaxiOfAnotherClass(orderDTO);
            isTaxiFound = setAttribute(session, taxis, orderDTO, user, true);
        }
        if(!isTaxiFound){
            logger.info("Taxi of another class weren`t found");
            taxis = taxiService.findSeveralTaxi(orderDTO);
            setAttribute(session, taxis, orderDTO, user,false);
        }
        return "redirect:/user/order-taxi/view-new-order";
    }

    /**
     * Method for retrieving prepared order`s page
     * @return prepared order`s view
     */
    @GetMapping("/order-taxi/view-new-order")
    public String viewOrder() {
        return "users/viewdetails";
    }

    /**
     * Method for approving new order
     * @param index - order index
     * @param session - HTTTP Session
     * @param redirectAttributes - redirect attributes
     * @return view according to method result
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/order-taxi/new-order")
    public String makeOrder(@RequestParam(name = "index") int index, HttpSession session, RedirectAttributes redirectAttributes) {
        Order order = ((List<Order>) session.getAttribute("order")).get(index);
        User user = getUser();
        if (getUser().getBalance().compareTo(order.getTotal())>=0) {
            session.removeAttribute("order");
            session.removeAttribute("isFound");
            try {
                orderService.saveOrder(order);
            }catch(TaxiBusyException | UserNotFoundException | NotEnoughMoneyException exception){
                logger.error("Data is deprecated. Removing prepared order");
                redirectAttributes.addFlashAttribute("orderError", true);
                return "redirect:/user/order-taxi/view-new-order";
            }
            return "redirect:/user/orders";
        }
        logger.info("User doesn`t have enough money. Redirecting to recharge page");
        redirectAttributes.addFlashAttribute("total", order.getTotal().subtract(user.getBalance()));
        return "redirect:/user/recharge-balance";
    }

    /**
     * Method for cancelling prepared orders
     * @param index order`s index
     * @param session HTTP Session
     * @return prepared order`s view
     */
    @SuppressWarnings("unchecked")
    @PostMapping("/order-taxi/cancel-order")
    public String cancelOrder(@RequestParam(name = "index") int index, HttpSession session) {
        logger.info("Cancelling order");
        List<Order> orders = (List<Order>) session.getAttribute("order");
        orders.remove(index);
        if(orders.isEmpty()){
            logger.warn("No more available orders left");
            session.removeAttribute("isFound");
            session.removeAttribute("order");
        }
        return "redirect:/user/order-taxi/view-new-order";
    }

    /**
     * Method for retrieving user`s orders and sorting them
     * @param page - page to be loaded
     * @param sort - column by which orders will be sorted
     * @param direction - sorting direction
     * @param model - model
     * @return user`s orders view
     */
    @GetMapping("/orders")
    public String viewOrder(@RequestParam(name = "page", defaultValue = "0") Integer page,
                            @RequestParam(name = "sort", defaultValue = "id") String sort,
                            @RequestParam(name = "sortDirection", defaultValue = "asc") String direction,
                            Model model) {
        logger.info("Retrieving orders page");
        Page<Order> orders = orderService.getPaginatedOrdersByUser(page, sort, direction, getUser());
        model.addAttribute("orders", orders);
        model.addAttribute("sort", sort);
        model.addAttribute("sortDirection", direction);
        return "users/vieworders";
    }

    /**
     * Method for cancelling user`s order
     * @param id - order`s id
     * @return order`s view
     */
    @PostMapping("/orders/cancel")
    public String cancelActiveOrder(@RequestParam(name = "id") Long id) {
        logger.info("Cancelling order, id = " + id);
        orderService.processOrder(id,true);
        return "redirect:/user/orders";
    }

    /**
     * Method for finishing active order
     * @param id - active order`s id
     * @return order`s view
     */
    @PostMapping("/orders/finish")
    public String finishActiveOrder(@RequestParam(name = "id") Long id) {
        logger.info("Finishing order, id = " + id);
        orderService.processOrder(id,false);
        return "redirect:/user/orders";
    }

    /**
     * Method that provides user data for the model
     * @return user object
     */
    @ModelAttribute("user")
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) userService.loadUserByUsername(authentication.getName());
    }

    /**
     * Method for order formation
     * @param session - HTTP Session
     * @param taxis - set of taxis
     * @param orderDTO - order DTO
     * @param user - user
     * @param isMultipleOrders - is need to create multiple orders
     * @return true if order formed, otherwise - false
     */
    private boolean setAttribute(HttpSession session, Set<Taxi> taxis, OrderDTO orderDTO, User user, boolean isMultipleOrders){
        List<Order> orders = new ArrayList<>();
        if (taxis.isEmpty()) {
            return false;
        }
        if(isMultipleOrders){
            taxis.forEach(t -> orders.add(orderService.prepareOrder(orderDTO, Collections.singleton(t), user)));
        }else{
            orders.add(orderService.prepareOrder(orderDTO, taxis, user));
        }
        session.setAttribute("isFound", true);
        session.setAttribute("order", orders);
        return true;
    }

}
