package ua.training.springproject.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    /**
     * Order service object
     */
    private final OrderService orderService;

    /**
     * User service object
     */
    private final UserService userService;

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(AdminController.class);

    /**
     * Constructor for dependency injection
     * @param orderService - OrderService`s bean
     * @param userService - UserServices`s bean
     */
    @Autowired
    public AdminController(OrderService orderService, UserService userService){
        this.orderService = orderService;
        this.userService = userService;
    }

    /**
     * Method for retrieving page, that contains orders sorted and filtered according to DTO
     * @param pageInfoDTO - DTO
     * @param model - model
     * @return view with all orders
     */
    @GetMapping("/")
    public String index(@ModelAttribute(name = "pageInfoDTO") PageInfoDTO pageInfoDTO, Model model) {
        logger.info("Admin reached orders page");
        Page<Order> orders = orderService.getPaginatedOrders(pageInfoDTO, orderService.makePredicate(pageInfoDTO));
        model.addAttribute("orders", orders);
        model.addAttribute("pageInfoDTO", pageInfoDTO);
        return "admins/admin";
    }

    /**
     * Method that is used for finishing active order
     * @param id - order`s id
     * @return view with all orders
     */
    @PostMapping("/orders/finish")
    public String finishActiveOrder(@RequestParam(name = "id") Long id) {
        logger.info("Trying to finish order № " + id);
        orderService.processOrder(id,false);
        return "redirect:/admin/";
    }

    /**
     * Method that provides page info data for the model
     * @return PageInfoDTO object
     */
    @ModelAttribute("pageInfoDTO")
    public PageInfoDTO getPageInfoDTO(){
        return new PageInfoDTO();
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

}
