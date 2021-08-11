package ua.training.springproject.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    /**
     * Logger
     */
    private static final Logger logger = LogManager.getLogger(CustomErrorController.class);

    /**
     * Method for retrieving error page
     * @param request - servlet request
     * @param model - model
     * @return error view
     */
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("pageNotFoundError", true);
                logger.error("Page not found");
            }else if(statusCode == HttpStatus.FORBIDDEN.value()){
                model.addAttribute("pageForbiddenError", true);
                logger.error("Page is forbidden");
            }else{
                model.addAttribute("commonError", true);
                logger.error("Error has occurred");
            }
        }
        return "error/error";
    }
}
