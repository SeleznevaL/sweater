package ru.selezneva.sweater.service;

import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.security.UserRole;

import java.util.List;
import java.util.Set;

public interface UserService {
    void save(UserDto userDto);
    UserDto getById(Long userId);
    List<UserDto> findAll();
    void updateRoles(Long userId, Set<UserRole> roles);
}
