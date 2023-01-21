package org.launchcode.VolunteerOrganizer.service;

import org.launchcode.VolunteerOrganizer.models.User;

import javax.servlet.http.HttpSession;
import java.util.Optional;

public interface UserService {

    //TODO these should be configured via yaml and passed in as a config object
    public static final String USER_SESSION_KEY = "userId";
    public static final String USER_ACCT_TYPE = "accountType";

    Optional<User> of(HttpSession httpSession);
    Optional<User> findByUsername(String username);
    void delete(User user);
    void save(User user);

}
