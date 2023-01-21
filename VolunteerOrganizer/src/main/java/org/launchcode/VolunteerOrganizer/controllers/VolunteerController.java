package org.launchcode.VolunteerOrganizer.controllers;

import org.launchcode.VolunteerOrganizer.models.Opportunity;
import org.launchcode.VolunteerOrganizer.models.User;
import org.launchcode.VolunteerOrganizer.models.data.OpportunityRepository;
import org.launchcode.VolunteerOrganizer.models.dto.OpportunityUserDTO;
import org.launchcode.VolunteerOrganizer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Optional;

@RequestMapping("volunteer")
@Controller
public class VolunteerController {

    private final OpportunityRepository opportunityRepository;
    private final UserService userService;

    public VolunteerController(OpportunityRepository opportunityRepository,
                               UserService userService) {
        this.opportunityRepository = opportunityRepository;
        this.userService = userService;
    }

    @GetMapping("/registered-opportunities")
    public String displayRegisteredOpportunities(HttpServletRequest request, Model model) {
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("user", user);

        Iterable<Opportunity> allOpportunities = opportunityRepository.findAll();
        ArrayList<Opportunity> registeredOpportunities = new ArrayList<>();

        for (Opportunity opportunity : allOpportunities) {
            if (opportunity.getVolunteers().contains(user)) {
                registeredOpportunities.add(opportunity);
            }
        }

        if (!registeredOpportunities.isEmpty()) {
            model.addAttribute("opportunities", registeredOpportunities);
        }
        return "registered-opportunities";
    }

    @GetMapping("/sign-up")
    public String volunteerSignup(HttpServletRequest request, @RequestParam Integer opportunityId, Model model){
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("user", user);

        Optional<Opportunity> result = opportunityRepository.findById(opportunityId);

        if(result.isEmpty()) {
            model.addAttribute("redirectMessageFailure", "Sign Up Unuccessful! Volunteer Opportunity Does Not Exist.");
            return "home";
        }

        Opportunity opportunity = result.get();
        OpportunityUserDTO opportunityVolunteer = new OpportunityUserDTO();
        opportunityVolunteer.setOpportunity(opportunity);

        if (!opportunity.getVolunteers().contains(user)) {
            if (opportunity.getNumVolunteerSlotsRemaining() > 0) {
                opportunity.addVolunteer(user);
                opportunityRepository.save(opportunity);
                return "redirect:./registered-opportunities";
            } else {
                model.addAttribute("redirectMessageFailure", "Sign Up Unuccessful! No remaining volunteer slots.");
                return "home";
            }
        } else {
            model.addAttribute("redirectMessageFailure", "Sign Up Unuccessful! Already registered for this volunteer opportunity.");
            return "home";
        }
    }
    
    @GetMapping("/unregister")
    public String volunteerUnregister(HttpServletRequest request, @RequestParam Integer opportunityId, Model model){
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("user", user);

        Optional<Opportunity> result = opportunityRepository.findById(opportunityId);

        if(result.isEmpty()) {
            model.addAttribute("redirectMessageFailure", "Unuccessful! Volunteer Opportunity Does Not Exist.");
            return "home";
        }

        Opportunity opportunity = result.get();
        OpportunityUserDTO opportunityVolunteer = new OpportunityUserDTO();
        opportunityVolunteer.setOpportunity(opportunity);

        if (opportunity.getVolunteers().contains(user)) {
            opportunity.removeVolunteer(user);
            opportunityRepository.save(opportunity);
            return "redirect:./registered-opportunities";
        } else {
            model.addAttribute("redirectMessageFailure", "You are not registered for this volunteer opportunity! Cannot unregister.");
            return "home";
        }
    }
}