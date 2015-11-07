package org.persac.web.controller;

import org.apache.log4j.Logger;
import org.persac.persistence.model.User;
import org.persac.service.UserService;
import org.persac.service.exception.EmailExistsException;
import org.persac.service.exception.UsernameExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: maks
 * date: 10.10.15
 */

@Controller
@RequestMapping("/")
@ControllerAdvice
public class WelcomeController {

    Logger logger = Logger.getLogger(IndexController.class.getName());

    @Autowired
    private UserService userService;

    @RequestMapping("welcome")
    public String welcome(Model model, @RequestParam(value = "error", required = false) String error) {
        User user = new User();
        model.addAttribute("user", user);

        if (error != null) {
            model.addAttribute("errMsg", "Invalid username or password.");
        }

        return "welcome";
    }

    @RequestMapping(value = "/register-account", method = RequestMethod.POST)
    public ModelAndView registerUserAccount(@ModelAttribute("user") User user, BindingResult result,
                                            WebRequest request, Errors errors) {
        //Dummy validation. TODO: use more intelligent validation
        if (null == user.getName() || "".equals(user.getName())) {
            result.rejectValue("name", "validation.message.username.empty");
        }

        /*if (null == user.getFirstName() || "".equals(user.getFirstName())) {
            result.rejectValue("firstName", "validation.message.firstName.empty");
        }

        if (null == user.getLastName() || "".equals(user.getLastName())) {
            result.rejectValue("lastName", "validation.message.lastName.empty");
        }*/

        String EMAIL_PATTERN = "^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$";
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            result.rejectValue("email", "validation.message.email.format");
        }

        if (null == user.getPassword() || "".equals(user.getPassword())) {
            result.rejectValue("password", "validation.message.password.empty");
        }

        if (!user.getPassword().equals(user.getMatchingPassword())) {
            result.rejectValue("matchingPassword", "validation.message.password.not.match");
        }


        if (!result.hasErrors()) {
            try {
                //todo: just can be called service method
                createUserAccount(user, result);
            } catch (EmailExistsException e) {
                result.rejectValue("email", "validation.message.email.exists");
            } catch (UsernameExistsException e) {
                result.rejectValue("name", "validation.message.username.exists");
            }
        }


        if (result.hasErrors()) {
            return new ModelAndView("welcome", "user", user);
        } else {
            return new ModelAndView("index", "user", user);
        }
    }

    private User createUserAccount(User user, BindingResult result) throws EmailExistsException,
                                                                           UsernameExistsException {
        User registered = userService.registerNewUserAccount(user);
        return registered;
    }
}