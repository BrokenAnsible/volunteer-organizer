package org.launchcode.VolunteerOrganizer.controllers;

import org.launchcode.VolunteerOrganizer.models.Opportunity;
import org.launchcode.VolunteerOrganizer.models.User;
import org.launchcode.VolunteerOrganizer.models.data.OpportunityRepository;
import org.launchcode.VolunteerOrganizer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.util.List;
import java.util.Optional;

@RequestMapping("manage")
@Controller
public class ManageController {

    private final OpportunityRepository opportunityRepository;
    private final UserService userService;

    public ManageController(OpportunityRepository opportunityRepository,
                            UserService userService) {
        this.opportunityRepository = opportunityRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public String displayManageOpportunities(HttpServletRequest request, Model model) {
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        List<Opportunity> opportunity = user.getOpportunitiesForUser(opportunityRepository);
        model.addAttribute("title", "Manage Volunteer Opportunities");
        model.addAttribute("user", user );
        model.addAttribute("displayManageOpportunityButtons", true);
        model.addAttribute("opportunities", opportunity);
        return "manage";
    }

    @GetMapping("/delete-opportunity")
    public String processDeleteOpportunities(HttpServletRequest request,@RequestParam int opportunityId, Model model ) {
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("user", user );

        Optional<Opportunity> optOpportunity = opportunityRepository.findById(opportunityId);
        if (optOpportunity.isPresent()) {
            Opportunity opportunity = (Opportunity) optOpportunity.get();
            if (user.getId() == opportunity.getCreatorUserId()) {
                opportunityRepository.delete(opportunity);
                return "redirect:";
            } else {
                model.addAttribute("redirectMessageFailure", "You are not the creator of that Volunteer Opportunity! Cannot delete.");
                return "home";
            }
        } else {
            model.addAttribute("redirectMessageFailure", "Unuccessful! Volunteer Opportunity Does Not Exist.");
            return "home";
        }
    }

    @GetMapping("/edit-opportunity")
    public String displayEditOpportunityForm(HttpServletRequest request,@RequestParam int opportunityId, Model model ) {
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("user", user );

        Optional<Opportunity> optOpportunity = opportunityRepository.findById(opportunityId);
        if (optOpportunity.isPresent()) {
            Opportunity opportunity = (Opportunity) optOpportunity.get();
            if (user.getId() == opportunity.getCreatorUserId()) {
                model.addAttribute("title", "Edit Volunteer Opportunity");
                model.addAttribute("opportunity", opportunity);
                return "create";
            } else {
                model.addAttribute("redirectMessageFailure", "You are not the creator of that Volunteer Opportunity! Cannot edit.");
                return "home";
            }
        } else {
            model.addAttribute("redirectMessageFailure", "Unuccessful! Volunteer Opportunity Does Not Exist.");
            return "home";
        }
    }

    @PostMapping("/edit-opportunity")
    public String processEditOpportunityForm(HttpServletRequest request,@ModelAttribute @Valid Opportunity opportunityEdits, Errors errors, @RequestParam int opportunityId, Model model){
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("user", user );
        
        if(errors.hasErrors()) {
            model.addAttribute("title", "Edit Volunteer Opportunity:");
            return "create";
        }

        Optional<Opportunity> optOpportunity = opportunityRepository.findById(opportunityId);
        Opportunity opportunity = optOpportunity
                .orElseThrow(() -> new RuntimeException("opportunity not found")); //TODO you might was to do a getOrDefault

        //TODO this is where a RestController would be good, where you let the client post to rest endpoints
        //and then send a status code. That would be a better use of the Optional.orElseThrow and then
        //you pair that with a Excpetion Handler that intercepts specific exceptions and then return a specific status
        //code

        opportunity.setAge(opportunityEdits.getAge());
        opportunity.setCategory(opportunityEdits.getCategory());
        opportunity.setCity(opportunityEdits.getCity());
        opportunity.setDescription(opportunityEdits.getDescription());
        opportunity.setEndDate(opportunityEdits.getEndDate());
        opportunity.setHours(opportunityEdits.getHours());
        opportunity.setNumVolunteersNeeded(opportunityEdits.getNumVolunteersNeeded());
        opportunity.setStartDate(opportunityEdits.getStartDate());

        opportunityRepository.save(opportunity);

        return "redirect:";
    }
}