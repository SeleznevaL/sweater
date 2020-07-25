package ru.selezneva.sweater.service.impl;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.entity.User;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.security.UserRole;
import ru.selezneva.sweater.service.UserService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final Converter<User, UserDto> userUserDtoConverter;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepo userRepo, Converter<User, UserDto> userUserDtoConverter, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userUserDtoConverter = userUserDtoConverter;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    private void createAdmin() {
        if (userRepo.findByUserName("admin") == null) {
            userRepo.saveAndFlush(
                    new User().setRoles(Collections.singleton(UserRole.ADMIN))
                            .setUserName("admin").setActive(true)
                            .setPassword(passwordEncoder.encode("admin"))
            );
        }
    }

    @Override
    @Transactional
    public void save(UserDto userDto) {
        Set<UserRole> roles = new HashSet<>();
        for (UserRole userRole : userDto.getRoles().keySet()) {
            if (userDto.getRoles().get(userRole)) {
                roles.add(userRole);
            }
        }
        User user = new User().setUserName(userDto.getUserName())
                .setActive(true)
                .setPassword(passwordEncoder.encode(userDto.getPassword()))
                .setRoles(roles);
        userRepo.save(user);
    }

    @Override
    public UserDto getById(Long userId) {
        return userUserDtoConverter.convert(userRepo.getOne(userId));
    }

    @Override
    public List<UserDto> findAll() {
        userRepo.findAll();
        return userRepo.findAll().stream().map(userUserDtoConverter::convert).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateRoles(Long userId, Set<UserRole> roles) {
        User user = userRepo.getOne(userId);
        user.setRoles(roles);
        userRepo.flush();
    }
}
