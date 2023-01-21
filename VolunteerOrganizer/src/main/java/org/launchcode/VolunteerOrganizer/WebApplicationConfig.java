package org.launchcode.VolunteerOrganizer;

import org.launchcode.VolunteerOrganizer.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebApplicationConfig implements WebMvcConfigurer {

    private final UserService userService;

    public WebApplicationConfig(UserService userService) {
        this.userService = userService;
    }

    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor( authenticationFilter() );
    }

}

