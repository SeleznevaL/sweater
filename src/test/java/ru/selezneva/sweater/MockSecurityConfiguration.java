package ru.selezneva.sweater;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.selezneva.sweater.security.CustomGrantedAuthority;
import ru.selezneva.sweater.security.CustomUserDetails;
import ru.selezneva.sweater.security.UserRole;

import java.util.ArrayList;
import java.util.List;


@Configuration
public class MockSecurityConfiguration {
    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                List<CustomGrantedAuthority> roles = new ArrayList<>();
                roles.add(new CustomGrantedAuthority(UserRole.USER));
                if (username.equals("admin")) {
                    roles.add(new CustomGrantedAuthority(UserRole.ADMIN));
                    return new CustomUserDetails(
                            1L,
                            "admin",
                            "$2a$10$U.bSg9iUvJs8xLTj0ilJ2.T9llzuThDaM7UsQkXfv29SZoorGhKGy",
                            roles
                    );
                }
                return new CustomUserDetails(
                        2L,
                        "user",
                        "$2a$10$CzwCGmrLMGieNR8tNQ9/leht84J.VVVN6a3m4LTmUgVWZTGsga3V6",
                        roles
                );
            }
        };
    }
}

