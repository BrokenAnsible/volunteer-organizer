package org.launchcode.VolunteerOrganizer.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.launchcode.VolunteerOrganizer.models.Opportunity;
import org.launchcode.VolunteerOrganizer.models.User;
import org.launchcode.VolunteerOrganizer.models.data.OpportunityRepository;
import org.launchcode.VolunteerOrganizer.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequestMapping("account")
@Controller
public class AccountController {

    private final OpportunityRepository opportunityRepository;
    private final UserService userService;

    private AccountController(OpportunityRepository opportunityRepository,
                              UserService userService) {
        this.opportunityRepository = opportunityRepository;
        this.userService = userService;
    }

    @GetMapping("")
    public String displayAccount(HttpServletRequest request, Model model) {
        User user = userService.of(request.getSession())
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));
        model.addAttribute("title", "Account Details");
        model.addAttribute("user", user);
        return "account";
    }

    @GetMapping("/delete")
    public String processDeleteAccount(HttpServletRequest request){
        HttpSession session = request.getSession();
        User user = userService.of(session)
                .orElseThrow(() -> new RuntimeException("Unauthorized Access"));

        if (user.getAccountType().equals("organization")) {
            List<Opportunity> opportunities = user.getOpportunitiesForUser(opportunityRepository);
            for (Opportunity opportunity : opportunities) {
                Optional<Opportunity> optOpportunity = opportunityRepository.findById(opportunity.getId());
                if (optOpportunity.isPresent()) {
                    Opportunity opportunityToDelete = optOpportunity.get();
                    opportunityRepository.delete(opportunityToDelete);
                }   
            }
        } else if (user.getAccountType().equals("volunteer")) {
            Iterable<Opportunity> allOpportunities = opportunityRepository.findAll();
            for (Opportunity opportunity : allOpportunities) {
                if (opportunity.getVolunteers().contains(user)) {
                    opportunity.removeVolunteer(user);
                }
            }

        }
        session.invalidate();
        userService.delete(user);
        return "redirect:/";
    }
}