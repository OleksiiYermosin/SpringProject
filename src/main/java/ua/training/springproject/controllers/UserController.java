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
import ua.training.springproject.dto.OrderDTO;
import ua.training.springproject.dto.ValueDTO;
import ua.training.springproject.entities.Order;
import ua.training.springproject.entities.Taxi;
import ua.training.springproject.entities.TaxiStatus;
import ua.training.springproject.entities.User;
import ua.training.springproject.services.OrderService;
import ua.training.springproject.services.TaxiService;
import ua.training.springproject.services.UserService;

import javax.validation.Valid;
import java.util.Arrays;
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
    public String getRechargePage(Model model){
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
    public String getOrders(@ModelAttribute("orderDTO") @Valid OrderDTO orderDTO, BindingResult bindingResult, Model model){
        if (bindingResult.hasErrors()){
            return "users/ordertaxi";
        }
        System.out.println(orderDTO);
        Optional<Taxi> taxi = taxiService.findSuitableCar(orderDTO);
        if(taxi.isPresent()){
            Order order = orderService.prepareOrder(orderDTO, new HashSet<>(Collections.singletonList(taxi.get())), getUser());
            model.addAttribute("order", order);
            return "users/viewdetails";
        }
        return "redirect:/user/";
    }

    @GetMapping("/view")
    public String getView(Model model){
        model.addAttribute("value", "sc");
        return "users/viewdetails";
    }

    @ModelAttribute("user")
    public User getUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.findUserByUsername(authentication.getName());
    }

}