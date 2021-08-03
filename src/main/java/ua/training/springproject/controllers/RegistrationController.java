package ua.training.springproject.controllers;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ua.training.springproject.entities.User;
import ua.training.springproject.services.UserService;

import javax.validation.Valid;

@Controller
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/registration")
    public String addUser(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()){
            System.out.println("In if");
            return "registration";
        }
        if (!userService.saveUser(user)){
            model.addAttribute("usernameError", "User is already exist");
            return "registration";
        }

        return "redirect:/login";
    }

}
