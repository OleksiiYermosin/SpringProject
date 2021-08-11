package ua.training.springproject.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.training.springproject.entities.User;

@Controller
public class BaseController {

    private static final Logger logger = LogManager.getLogger(BaseController.class);

    /**
     * Method for retrieving main page
     * @return main page view
     */
    @GetMapping("/")
    public String index() {
        return "common/main";
    }

    /**
     * Method for retrieving login page
     * @return login view
     */
    @GetMapping("/login")
    public String login() {
        return "common/login";
    }

    /**
     * Method for retrieving page with error display
     * @param model - model
     * @return error page view
     */
    @GetMapping("/login-error")
    public String loginError(Model model) {
        logger.error("Error has occurred during login");
        model.addAttribute("loginError", true);
        return "common/login";
    }

}
