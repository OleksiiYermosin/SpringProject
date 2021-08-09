package ua.training.springproject.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.training.springproject.dto.PageInfoDTO;
import ua.training.springproject.entities.Order;
import ua.training.springproject.entities.User;
import ua.training.springproject.services.OrderService;
import ua.training.springproject.services.UserService;


@Controller
@RequestMapping("/admin")
public class AdminController {

    private final OrderService orderService;
    private final UserService userService;

    @Autowired
    public AdminController(OrderService orderService, UserService userService){
        this.orderService = orderService;
        this.userService = userService;
    }


    @GetMapping("/")
    public String index(@ModelAttribute(name = "pageInfoDTO") PageInfoDTO pageInfoDTO, Model model) {
        Page<Order> orders = orderService.getPaginatedOrders(pageInfoDTO, orderService.makePredicate(pageInfoDTO));
        model.addAttribute("orders", orders);
        model.addAttribute("pageInfoDTO", pageInfoDTO);
        return "admins/admin1";
    }

    @PostMapping("/orders/finish")
    public String finishActiveOrder(@RequestParam(name = "id") Long id) {
        orderService.processOrder(id,false);
        return "redirect:/admin/";
    }

    @ModelAttribute("pageInfoDTO")
    public PageInfoDTO getPageInfoDTO(){
        return new PageInfoDTO();
    }

    @ModelAttribute("user")
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) userService.loadUserByUsername(authentication.getName());
    }

}
