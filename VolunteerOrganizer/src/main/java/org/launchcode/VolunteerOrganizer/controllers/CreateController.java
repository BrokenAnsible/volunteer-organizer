package org.launchcode.VolunteerOrganizer.controllers;

import org.launchcode.VolunteerOrganizer.models.Opportunity;
import org.launchcode.VolunteerOrganizer.models.User;
import org.launchcode.VolunteerOrganizer.models.data.OpportunityRepository;
import org.launchcode.VolunteerOrganizer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequestMapping("create")
public class CreateController {

    private final OpportunityRepository opportunityRepository;
    private final UserService userService;

    public CreateController(OpportunityRepository opportunityRepository,
                            UserService userService) {
        this.opportunityRepository = opportunityRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public String renderCreateOpportunityForm(HttpServletRequest request, Model model){
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("title", "Create Volunteer Opportunity:");
        model.addAttribute("opportunity", new Opportunity());
        model.addAttribute("user", user);
        return "create";
    }

    @PostMapping("")
    public String processCreateOpportunityForm(HttpServletRequest request,@ModelAttribute @Valid Opportunity opportunity, Errors errors, Model model){
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        opportunity.setCreatorUserId(user.getId());
        if(errors.hasErrors()) {
            model.addAttribute("title", "Create Volunteer Opportunity:");
            return "create";
        }
        opportunity.setName(user.getOrganizationName());
        opportunityRepository.save(opportunity);
        return "redirect:/home";
    }
}