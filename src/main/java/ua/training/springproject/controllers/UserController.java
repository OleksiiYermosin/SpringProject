package ua.training.springproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.dto.ValueDTO;
import ua.training.springproject.entities.Order;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.User;
import ua.training.springproject.services.OrderService;
import ua.training.springproject.services.TaxiService;
import ua.training.springproject.services.UserService;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;


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
    public String index(){
        return "users/user";
    }

    @GetMapping("/recharge-balance")
    public String getRechargePage(BigDecimal total, Model model){
        if (total!=null){
            model.addAttribute("total", total);
        }
        model.addAttribute("valueDTO", new ValueDTO());
        return "users/addmoney";
    }

    @PostMapping("/recharge-balance")
    public String addMoney(@ModelAttribute("valueDTO") @Valid ValueDTO valueDTO, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "users/addmoney";
        }
        userService.updateUserBalance(getUser().getId(), valueDTO.getValue());
        return "redirect:/user/";
    }

    @GetMapping("/order-taxi")
    public String getOrderPage(Model model){
        model.addAttribute("orderDTO", new OrderDTO());
        return "users/ordertaxi";
    }

    @PostMapping("/order-taxi")
    public String getOrders(@ModelAttribute("orderDTO") @Valid OrderDTO orderDTO, BindingResult bindingResult, HttpSession session){
        System.out.println("Before errors");
        if (bindingResult.hasErrors()){
            return "users/ordertaxi";
        }
        Optional<Taxi> taxi = taxiService.findSuitableCar(orderDTO);
        if(taxi.isPresent()){
            Order order = orderService.prepareOrder(orderDTO, new HashSet<>(Collections.singletonList(taxi.get())), getUser());
            session.setAttribute("order", order);
            return "redirect:/user/order-taxi/view-new-order";
        }
        return "redirect:/user/";
    }

    @GetMapping("/order-taxi/view-new-order")
    public String viewOrder(){
        return "users/viewdetails";
    }

    @PostMapping("/order-taxi/new-order")
    public String makeOrder(HttpSession session, RedirectAttributes redirectAttributes){
        BigDecimal total = ((Order) session.getAttribute("order")).getTotal();
        User user = getUser();
        if(userService.getMoneyFromUser(total, user.getId())){
            orderService.saveOrder((Order) session.getAttribute("order"));
            session.removeAttribute("order");
            return "redirect:/user/";
        }
        redirectAttributes.addFlashAttribute("total", total.subtract(user.getBalance()));
        return "redirect:/user/recharge-balance";
    }

    @PostMapping("/order-taxi/cancel-order")
    public String cancelOrder(HttpSession session){
        session.removeAttribute("order");
        return "redirect:/user/";
    }

    @ModelAttribute("user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) userService.loadUserByUsername(authentication.getName());
    }

}
