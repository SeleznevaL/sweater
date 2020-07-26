package ru.selezneva.sweater.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.selezneva.sweater.converters.UserToUserDtoConverter;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.entity.User;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.security.UserRole;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    @Mock
    UserRepo userRepo;

    Converter<User, UserDto> userUserDtoConverter = new UserToUserDtoConverter();

    @Mock
    PasswordEncoder passwordEncoder;

    UserServiceImpl subj;

    User admin;
    User user;
    UserDto userDto;
    UserDto adminDto;
    List<User> users;
    List<UserDto> userDtos;

    @Before
    public void setUp() throws Exception {
        subj = new UserServiceImpl(userRepo, userUserDtoConverter, passwordEncoder);
        when(passwordEncoder.encode("user")).thenReturn("$2a$10$cAtSqdWyeJOlZbW8bn9BZOyTtWSB0ZzgxQZX7DDnPw3wP92UUNOai");
        admin = new User().setRoles(Collections.singleton(UserRole.ADMIN))
                .setUserName("admin").setActive(true).setId(1L)
                .setPassword("$2a$10$NdRzbc2o9w.RLvDLp08lSOdezO.a7YDqIeCPyNLSahwg4LPTUGpPu");
        user = new User().setUserName("user")
                .setActive(true).setId(2L)
                .setPassword("$2a$10$cAtSqdWyeJOlZbW8bn9BZOyTtWSB0ZzgxQZX7DDnPw3wP92UUNOai")
                .setRoles(Collections.singleton(UserRole.USER));
        Map<UserRole, Boolean> userRoles = new HashMap<>();
        userRoles.put(UserRole.ADMIN, false);
        userRoles.put(UserRole.USER, true);
        userDto = new UserDto().setActive(true).setId(2L)
                .setPassword("$2a$10$cAtSqdWyeJOlZbW8bn9BZOyTtWSB0ZzgxQZX7DDnPw3wP92UUNOai")
                .setUserName("user")
                .setRoles(userRoles);
        Map<UserRole, Boolean> adminRoles = new HashMap<>();
        adminRoles.put(UserRole.ADMIN, true);
        adminRoles.put(UserRole.USER, false);
        adminDto = new UserDto().setActive(true)
                .setId(1L)
                .setPassword("$2a$10$NdRzbc2o9w.RLvDLp08lSOdezO.a7YDqIeCPyNLSahwg4LPTUGpPu")
                .setRoles(adminRoles)
                .setUserName("admin");
        users = new ArrayList<>();
        users.add(user);
        users.add(admin);
        userDtos = new ArrayList<>();
        userDtos.add(userDto);
        userDtos.add(adminDto);

    }

    @Test
    public void save() {
        userDto.setPassword("user");
        userDto.setId(null);
        subj.save(userDto);
        verify(userRepo, times(1)).save(user.setId(null));
        verify(passwordEncoder, times(1)).encode("user");
    }

    @Test
    public void getById() {
        when(userRepo.getOne(1L)).thenReturn(admin);
        UserDto result = subj.getById(1L);
        Map<UserRole, Boolean> roles = new HashMap<>();
        roles.put(UserRole.USER, false);
        roles.put(UserRole.ADMIN, true);
        assertEquals(adminDto, result);
        verify(userRepo, times(1)).getOne(1L);
    }

    @Test
    public void findAll() {
        when(userRepo.findAll()).thenReturn(users);
        List<UserDto> all = subj.findAll();
        assertEquals(userDtos, all);
    }

    @Test
    public void updateRoles() {
        when(userRepo.getOne(2L)).thenReturn(user);
        subj.updateRoles(2L, Collections.singleton(UserRole.ADMIN));
        verify(userRepo, times(1)).getOne(2L);
        verify(userRepo, times(1)).flush();
    }
}