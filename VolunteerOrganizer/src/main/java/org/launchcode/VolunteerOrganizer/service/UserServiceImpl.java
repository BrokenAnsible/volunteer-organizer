package org.launchcode.VolunteerOrganizer.service;

import org.launchcode.VolunteerOrganizer.models.User;
import org.launchcode.VolunteerOrganizer.models.data.UserRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> of(HttpSession session) {
        Integer userId = (Integer) session.getAttribute(USER_SESSION_KEY);
        if (userId == null) { //default user won't have an id
            return Optional.empty();
        }

        return userRepository.findById(userId);
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        return Optional.ofNullable(user);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }
}
