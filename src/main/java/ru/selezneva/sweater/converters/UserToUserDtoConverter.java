package ru.selezneva.sweater.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.entity.User;
import ru.selezneva.sweater.security.UserRole;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserToUserDtoConverter implements Converter<User, UserDto> {
    @Override
    public UserDto convert(User user) {
        Map<UserRole, Boolean> roles = new HashMap<>();
        for (UserRole userRole : UserRole.values()) {
            if (user.getRoles().contains(userRole)) {
                roles.put(userRole, true);
            } else {
                roles.put(userRole, false);
            }
        }
        return new UserDto(user.getId(), user.getUserName(), user.getPassword(), user.isActive(), roles);
    }
}
