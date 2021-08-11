package ua.training.springproject.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.training.springproject.entities.User;
import ua.training.springproject.services.UserService;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    /**
     * User service object
     */
    private final UserService userService;

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(RegistrationController.class);

    /**
     * Constructor for dependency injection
     * @param userService - user service`s bean
     */
    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Method for retrieving page with registration form
     * @param model - model
     * @return registration form view
     */
    @GetMapping("/registration")
    public String registerUser(Model model) {
        logger.info("Reached registration page");
        model.addAttribute("user", new User());
        return "common/registration";
    }

    /**
     * Method for saving new user
     * @param user - user to be saved
     * @param bindingResult - binding result
     * @param model - model
     * @return login view
     */
    @PostMapping("/registration")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {

        if(bindingResult.hasErrors()){
            logger.error("Validation of registration form failed");
            return "common/registration";
        }
        if (!userService.saveUser(user)){
            logger.error("Registration failed. User is already exist");
            model.addAttribute("usernameError", "User is already exist");
            return "common/registration";
        }

        return "redirect:/login";
    }

}
