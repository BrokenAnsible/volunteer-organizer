package org.launchcode.VolunteerOrganizer.controllers;

import org.launchcode.VolunteerOrganizer.models.Opportunity;
import org.launchcode.VolunteerOrganizer.models.User;
import org.launchcode.VolunteerOrganizer.models.data.OpportunityRepository;
import org.launchcode.VolunteerOrganizer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller

public class ListController {

    private final OpportunityRepository opportunityRepository;
    private final UserService userService;

    public ListController(OpportunityRepository opportunityRepository,
                          UserService userService) {
        this.opportunityRepository = opportunityRepository;
        this.userService = userService;
    }

    @GetMapping("list")
    public String list(HttpServletRequest request, Model model) {
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        List<String> orgNames = new ArrayList<>();
        List<Opportunity> opportunitiesList = new ArrayList<>();
        Iterable<Opportunity> allOpportunity = opportunityRepository.findAll();
        for(Opportunity opportunity:allOpportunity) {
            if(!orgNames.contains(opportunity.getName())){
                orgNames.add(opportunity.getName());
                opportunitiesList.add(opportunity);
            }
        }
        model.addAttribute("user", user );
        model.addAttribute("opportunities",opportunitiesList );
        return "list";
    }

    @GetMapping("list-opportunity/{orgName}")
    public String listOpportunies(HttpServletRequest request, Model model, @PathVariable String orgName) {
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        List<Opportunity> opportunity= opportunityRepository.findByName(orgName);
        model.addAttribute("user", user );
        model.addAttribute("heading", "Opportunities for: "+ orgName );
        model.addAttribute("opportunities", opportunity);
        return "list-opportunity";
    }
}
