package ru.selezneva.sweater.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.selezneva.sweater.Application;
import ru.selezneva.sweater.MockSecurityConfiguration;
import ru.selezneva.sweater.config.SecurityConfiguration;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.security.UserRole;
import ru.selezneva.sweater.service.UserService;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@WebMvcTest(AdminController.class)
@Import({SecurityConfiguration.class, MockSecurityConfiguration.class})
public class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @InjectMocks
    AdminController subj;


    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    public void adminWithAdmin() throws Exception {
        List<UserDto> users = new ArrayList<>();
        Map<UserRole, Boolean> userRoles = new HashMap<>();
        userRoles.put(UserRole.USER, true);
        userRoles.put(UserRole.ADMIN, false);
        users.add(new UserDto().setActive(true)
                .setUserName("user")
                .setPassword("$2a$10$CzwCGmrLMGieNR8tNQ9/leht84J.VVVN6a3m4LTmUgVWZTGsga3V6")
                .setRoles(userRoles)
                .setId(2L));
        Map<UserRole, Boolean> adminRoles = new HashMap<>();
        adminRoles.put(UserRole.USER, true);
        adminRoles.put(UserRole.ADMIN, true);
        users.add(new UserDto().setActive(true)
                .setUserName("admin")
                .setPassword("$2a$10$U.bSg9iUvJs8xLTj0ilJ2.T9llzuThDaM7UsQkXfv29SZoorGhKGy")
                .setRoles(adminRoles)
                .setId(1L));
        when(userService.findAll()).thenReturn(users);
        mockMvc.perform(get("/admin")).andExpect(status().isOk())
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("username", "admin"))
                .andExpect(model().attribute("roles", UserRole.values()))
                .andExpect(view().name("admin"));
        verify(userService, times(1)).findAll();
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void adminWithUser() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithUserDetails(value = "admin", userDetailsServiceBeanName = "userDetailsService")
    public void editWithAdmin() throws Exception {
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("USER", "on");
        param.add("ADMIN", "on");
        param.add("id", "2");
        List<UserDto> users = new ArrayList<>();
        Map<UserRole, Boolean> userRoles = new HashMap<>();
        userRoles.put(UserRole.USER, true);
        userRoles.put(UserRole.ADMIN, true);
        users.add(new UserDto().setActive(true)
                .setUserName("user")
                .setPassword("$2a$10$CzwCGmrLMGieNR8tNQ9/leht84J.VVVN6a3m4LTmUgVWZTGsga3V6")
                .setRoles(userRoles)
                .setId(2L));
        users.add(new UserDto().setActive(true)
                .setUserName("admin")
                .setPassword("$2a$10$U.bSg9iUvJs8xLTj0ilJ2.T9llzuThDaM7UsQkXfv29SZoorGhKGy")
                .setRoles(userRoles)
                .setId(1L));
        when(userService.findAll()).thenReturn(users);
        mockMvc.perform(post("/edit").params(param))
                .andExpect(status().isOk())
                .andExpect(model().attribute("users", users))
                .andExpect(model().attribute("username", "admin"))
                .andExpect(model().attribute("roles", UserRole.values()))
                .andExpect(view().name("admin"));
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ADMIN);
        roles.add(UserRole.USER);
        verify(userService, times(1)).updateRoles(2L, roles);
        verify(userService, times(1)).findAll();
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void editWithUser() throws Exception {
        mockMvc.perform(post("/edit"))
                .andExpect(status().is4xxClientError());
    }
}