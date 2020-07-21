package ru.selezneva.sweater.dto;

import lombok.*;
import lombok.experimental.Accessors;
import ru.selezneva.sweater.security.UserRole;

import java.util.Map;
import java.util.Set;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String userName;
    private String password;
    private boolean active;
    private Map<UserRole, Boolean> roles;
}
