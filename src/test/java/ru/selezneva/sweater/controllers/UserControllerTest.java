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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.selezneva.sweater.Application;
import ru.selezneva.sweater.MockSecurityConfiguration;
import ru.selezneva.sweater.config.SecurityConfiguration;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.security.UserRole;
import ru.selezneva.sweater.service.UserService;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@WebMvcTest(UserController.class)
@Import({SecurityConfiguration.class, MockSecurityConfiguration.class})
public class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @InjectMocks
    UserController subj;

    @Test
    public void greeting() throws Exception {
        mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("greeting"));
    }

    @Test
    public void regForm() throws Exception {
        mockMvc.perform(get("/reg")).andExpect(status().isOk()).andExpect(view().name("reg"));
    }

    @Test
    public void reg() throws Exception {
        mockMvc.perform(post("/reg").param("username", "user")
                .param("password", "user"))
                .andExpect(status().is3xxRedirection()).andExpect(redirectedUrl("/login"));
        Map<UserRole, Boolean> roles = new HashMap<>();
        roles.put(UserRole.USER, true);
        verify(userService, times(1)).save(new UserDto().setUserName("user").setActive(true).setPassword("user").setRoles(roles));
    }

    @Test
    public void regNoParam() throws Exception {
        mockMvc.perform(post("/reg").param("username", "user"))
                .andExpect(status().is4xxClientError());
    }
}