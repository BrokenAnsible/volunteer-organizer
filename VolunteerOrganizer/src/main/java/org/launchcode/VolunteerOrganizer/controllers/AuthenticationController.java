package org.launchcode.VolunteerOrganizer.controllers;

import org.launchcode.VolunteerOrganizer.models.User;
import org.launchcode.VolunteerOrganizer.models.dto.CreateAccountDTO;
import org.launchcode.VolunteerOrganizer.models.dto.LoginFormDTO;
import org.launchcode.VolunteerOrganizer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class AuthenticationController {

    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    private static void setUserInSession(HttpSession session, User user) {
        session.setAttribute(UserService.USER_SESSION_KEY, user.getId());
        session.setAttribute(UserService.USER_ACCT_TYPE, user.getAccountType());
    }

    @SuppressWarnings("unused")
    @GetMapping("/")
    public String index(Model model) {

        return "index";
    }

    @GetMapping("/signup/{accountType}")
    public String index(Model model, @PathVariable String accountType) {
        CreateAccountDTO createAccountDTO = new CreateAccountDTO();
        String title = "New " + accountType.substring(0, 1).toUpperCase() + accountType.substring(1) + " User";
        createAccountDTO.setAccountType(accountType);
        if(accountType.equals("volunteer")) {
            createAccountDTO.setOrganizationName(null);
        }
        model.addAttribute("title", title);
        model.addAttribute("createAccountDTO", createAccountDTO);
        return "signup";
    }

    @PostMapping("/signup/{accountType}")
    public String processCreateAccountForm(@ModelAttribute @Valid CreateAccountDTO createAccountDTO,
                                   Errors errors, HttpServletRequest request, Model model) {

        String accountType = createAccountDTO.getAccountType();
        String title = "New " + accountType.substring(0, 1).toUpperCase() + accountType.substring(1) + " User";

        if (errors.hasErrors()) {
            model.addAttribute("title", title);
            return "signup";
        }

        Optional<User> theUser = userService.findByUsername(createAccountDTO.getUsername());
        if (theUser.isPresent()) {
            errors.rejectValue("username", "user.invalid", "The given username already exists");
            model.addAttribute("title", title);
            return "signup";
        }

        String password = createAccountDTO.getPassword();
        String verifyPassword = createAccountDTO.getVerifyPassword();

        if (!password.equals(verifyPassword)) {
            errors.rejectValue("password", "password.invalid", "" +
                    "Passwords do not match.");
            model.addAttribute("title", title);
            return "signup";
        }

        User newUser = new User(createAccountDTO.getUsername(), createAccountDTO.getPassword(),
                createAccountDTO.getAccountType(), createAccountDTO.getOrganizationName());

        userService.save(newUser);
        setUserInSession(request.getSession(), newUser);
        return "redirect:/home";
    }

    @GetMapping("/login")
    public String displayLoginForm(Model model) {
        model.addAttribute(new LoginFormDTO());
        model.addAttribute("title", "Log In");
        return "login";
    }

    @PostMapping("/login")
    public String processLoginForm(@ModelAttribute @Valid LoginFormDTO loginFormDTO,
                                   Errors errors, HttpServletRequest request,
                                   Model model) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Log In");
            return "login";
        }

        Optional<User> theUser = userService.findByUsername(loginFormDTO.getUsername());
        String password = loginFormDTO.getPassword();

        if (theUser.isEmpty() || !theUser.get().isMatchingPassword(password)) {
            model.addAttribute("invalidLoginMessage", "Invalid Username and Password Combination");
            model.addAttribute("title", "Log In");
            return "login";
        }

        setUserInSession(request.getSession(), theUser.get());

        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request){
        request.getSession().invalidate();
        return "redirect:/";
    }
}