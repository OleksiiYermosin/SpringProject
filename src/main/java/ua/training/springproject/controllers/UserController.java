package ua.training.springproject.controllers;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
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
import ua.training.springproject.services.OrderService;
import ua.training.springproject.services.TaxiService;
import ua.training.springproject.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;


@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final TaxiService taxiService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, TaxiService taxiService, OrderService orderService) {
        this.userService = userService;
        this.taxiService = taxiService;
        this.orderService = orderService;
    }

    @GetMapping("/")
    public String index() {
        return "users/user";
    }

    @GetMapping("/recharge-balance")
    public String getRechargePage(BigDecimal total, Model model) {
        if (total != null) {
            model.addAttribute("total", total);
        }
        model.addAttribute("valueDTO", new ValueDTO());
        return "users/addmoney";
    }

    @PostMapping("/recharge-balance")
    public String addMoney(@ModelAttribute("valueDTO") @Valid ValueDTO valueDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "users/addmoney";
        }
        userService.updateUserBalance(getUser().getId(), valueDTO.getValue());
        return "redirect:/user/";
    }

    @GetMapping("/order-taxi")
    public String getOrderPage(Model model) {
        model.addAttribute("orderDTO", new OrderDTO());
        return "users/ordertaxi";
    }

    @PostMapping("/order-taxi")
    public String getOrders(@ModelAttribute("orderDTO") @Valid OrderDTO orderDTO, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "users/ordertaxi";
        }
        Set<Taxi> taxis = new HashSet<>();
        User user = getUser();
        boolean isTaxiFound;
        session.setAttribute("isFound", false);
        taxiService.findSuitableCar(orderDTO).ifPresent(taxis::add);
        isTaxiFound = setAttribute(session, taxis, orderDTO, user, false);
        if(!isTaxiFound) {
            taxis = taxiService.findTaxiOfAnotherClass(orderDTO);
            isTaxiFound = setAttribute(session, taxis, orderDTO, user, true);
        }
        if(!isTaxiFound){
            taxis = taxiService.findSeveralTaxi(orderDTO);
            setAttribute(session, taxis, orderDTO, user,false);
        }
        return "redirect:/user/order-taxi/view-new-order";
    }

    @GetMapping("/order-taxi/view-new-order")
    public String viewOrder() {
        return "users/viewdetails";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/order-taxi/new-order")
    public String makeOrder(@RequestParam(name = "index") int index, HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        Order order = ((List<Order>) session.getAttribute("order")).get(index);
        User user = getUser();
        if (getUser().getBalance().compareTo(order.getTotal())>=0) {
            session.invalidate();
            try {
                orderService.saveOrder(order);
            }catch(TaxiBusyException | IllegalArgumentException | NotEnoughMoneyException exception){
                redirectAttributes.addFlashAttribute("orderError", true);
                return "redirect:/user/order-taxi/view-new-order";
            }
            return "redirect:/user/orders";
        }
        redirectAttributes.addFlashAttribute("total", order.getTotal().subtract(user.getBalance()));
        return "redirect:/user/recharge-balance";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/order-taxi/cancel-order")
    public String cancelOrder(@RequestParam(name = "index") int index, HttpSession session) {
        List<Order> orders = (List<Order>) session.getAttribute("order");
        orders.remove(index);
        if(orders.isEmpty()){
            session.removeAttribute("order");
        }
        return "redirect:/user/order-taxi/view-new-order";
    }

    @GetMapping("/orders")
    public String viewOrder(@RequestParam(name = "page", defaultValue = "0") Integer page,
                            @RequestParam(name = "sort", defaultValue = "id") String sort,
                            @RequestParam(name = "sortDirection", defaultValue = "asc") String direction,
                            Model model) {
        Page<Order> orders = orderService.getPaginatedOrdersByUser(page, sort, direction, getUser());
        model.addAttribute("orders", orders);
        model.addAttribute("sort", sort);
        model.addAttribute("sortDirection", direction);
        return "users/vieworders";
    }

    @PostMapping("/orders/cancel")
    public String cancelActiveOrder(@RequestParam(name = "id") Long id) {
        orderService.processOrder(id,true);
        return "redirect:/user/orders";
    }

    @PostMapping("/orders/finish")
    public String finishActiveOrder(@RequestParam(name = "id") Long id) {
        orderService.processOrder(id,false);
        return "redirect:/user/orders";
    }

    @ModelAttribute("user")
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) userService.loadUserByUsername(authentication.getName());
    }

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
