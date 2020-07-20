package ru.selezneva.sweater.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.selezneva.sweater.domain.User;
import ru.selezneva.sweater.repos.UserRepo;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepo.findByUserName(name);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь с именем " + name + "не найден.");
        }
        return new CustomUserDetails(
                user.getId(),
                user.getUserName(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(CustomGrantedAuthority::new)
                        .collect(Collectors.toList())
        );
    }
}

