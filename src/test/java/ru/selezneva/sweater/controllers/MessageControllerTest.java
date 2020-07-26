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
import ru.selezneva.sweater.dto.MessageDto;
import ru.selezneva.sweater.dto.UserDto;
import ru.selezneva.sweater.security.UserRole;
import ru.selezneva.sweater.service.MessageService;
import ru.selezneva.sweater.service.UserService;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
@WebMvcTest(MessageController.class)
@Import({SecurityConfiguration.class, MockSecurityConfiguration.class})
public class MessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    MessageService messageService;

    @MockBean
    UserService userService;

    @InjectMocks
    MessageController subj;

    UserDto user;
    List<MessageDto> messages;
    MessageDto messageDto1;
    MessageDto messageDto2;

    @Before
    public void setUp() throws Exception {
        Map<UserRole, Boolean> userRoles = new HashMap<>();
        userRoles.put(UserRole.USER, true);
        userRoles.put(UserRole.ADMIN, false);
        user = new UserDto().setActive(true)
                .setUserName("user")
                .setPassword("$2a$10$CzwCGmrLMGieNR8tNQ9/leht84J.VVVN6a3m4LTmUgVWZTGsga3V6")
                .setRoles(userRoles)
                .setId(2L);
        messages = new ArrayList<>();
        messageDto1 = new MessageDto().setUserName("user").setTime("2020-03-03 15:45:00.0").setText("test message").setTag("test").setId(1);
        messageDto2 = new MessageDto().setUserName("user").setTime("2020-03-07 15:20:00.0").setText("test message").setTag("test").setId(2);
        messages.add(messageDto1);
        messages.add(messageDto2);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void mainWithUser() throws Exception {
        when(userService.getById(2L)).thenReturn(user);
        when(messageService.findAll()).thenReturn(messages);
        mockMvc.perform(get("/main"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("messages", messages))
                .andExpect(model().attribute("username", "user"))
                .andExpect(view().name("main"));
        verify(userService, times(1)).getById(2L);
        verify(messageService, times(1)).findAll();
    }

    @Test
    public void mainWithoutUser() throws Exception {
        mockMvc.perform(get("/main"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
        verifyNoInteractions(userService);
        verifyNoInteractions(messageService);
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void addMessageWithUser() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", "test message");
        params.add("tag", "test");
        when(userService.getById(2L)).thenReturn(user);
        mockMvc.perform(post("/main").params(params))
                .andExpect(status().isOk())
                .andExpect(model().attribute("username", "user"))
                .andExpect(view().name("main"));
        verify(messageService, times(1)).save(messageDto1.setId(null).setTime(null));
        verify(userService, times(1)).getById(2L);
    }

    @Test
    public void addMessageWithoutUser() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("text", "test message");
        params.add("tag", "test");
        mockMvc.perform(post("/main").params(params))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
        verifyNoInteractions(messageService);
        verifyNoInteractions(userService);
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void filterWithUserFilterNotEmpty() throws Exception {
        when(messageService.findByTag("tag")).thenReturn(messages);
        mockMvc.perform(post("/messages").param("filter", "tag"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("filter", "tag"))
                .andExpect(model().attribute("messages", messages))
                .andExpect(view().name("messages"));
        verify(messageService, times(1)).findByTag("tag");
        verifyNoMoreInteractions(messageService);
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void filterWithUserFilterIsEmpty() throws Exception {
        when(messageService.findAll()).thenReturn(messages);
        mockMvc.perform(post("/messages").param("filter", ""))
                .andExpect(status().isOk())
                .andExpect(cookie().value("filter", ""))
                .andExpect(model().attribute("messages", messages))
                .andExpect(view().name("messages"));
        verify(messageService, times(1)).findAll();
        verifyNoMoreInteractions(messageService);
    }

    @Test
    public void filterWithoutUser() throws Exception {
        mockMvc.perform(post("/messages").param("filter", "tag"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
        verifyNoInteractions(messageService);
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void updateWithUserCookieNotEmpty() throws Exception {
        when(messageService.findByTag("tag")).thenReturn(messages);
        mockMvc.perform(get("/messages").cookie(new Cookie("filter", "tag")))
                .andExpect(status().isOk())
                .andExpect(model().attribute("messages", messages))
                .andExpect(view().name("messages"));
        verify(messageService, times(1)).findByTag("tag");
        verifyNoMoreInteractions(messageService);
    }

    @Test
    @WithUserDetails(value = "user", userDetailsServiceBeanName = "userDetailsService")
    public void updateWithUserCookieIsEmpty() throws Exception {
        when(messageService.findAll()).thenReturn(messages);
        mockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("messages", messages))
                .andExpect(view().name("messages"));
        verify(messageService, times(1)).findAll();
        verifyNoMoreInteractions(messageService);
    }

    @Test
    public void updateWithoutUser() throws Exception {
        mockMvc.perform(get("/messages").cookie(new Cookie("filter", "tag")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
        verifyNoInteractions(messageService);
    }
}