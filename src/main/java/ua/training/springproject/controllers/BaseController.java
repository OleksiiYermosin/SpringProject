package ua.training.springproject.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.training.springproject.entities.User;

@Controller
public class BaseController {

    @GetMapping("/")
    public String index() {
        return "common/main";
    }

    @GetMapping("/login")
    public String login() {
        return "common/login";
    }

    @GetMapping("/login-error")
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "common/login";
    }

    @GetMapping("/registration")
    public String registerUser(Model model) {
        model.addAttribute("user", new User());
        return "common/registration";
    }
}
