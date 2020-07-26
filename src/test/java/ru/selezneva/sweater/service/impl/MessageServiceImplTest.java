package ru.selezneva.sweater.service.impl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.convert.converter.Converter;
import ru.selezneva.sweater.converters.MessageToMessageDtoConverter;
import ru.selezneva.sweater.dto.MessageDto;
import ru.selezneva.sweater.entity.Message;
import ru.selezneva.sweater.entity.User;
import ru.selezneva.sweater.repos.MessageRepo;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.security.UserRole;
import ru.selezneva.sweater.service.MessageService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {

    @Mock
    MessageRepo messageRepo;
    @Mock
    UserRepo userRepo;

    MessageService subj;

    Converter<Message, MessageDto> messageToMessageDtoConverter = new MessageToMessageDtoConverter();

    List<Message> messages;
    List<MessageDto> messageDtos;
    User user;
    MessageDto messageDto1;
    MessageDto messageDto2;
    Message message1;
    Message message2;

    @Before
    public void setUp() throws Exception {
        subj = new MessageServiceImpl(messageRepo, userRepo, messageToMessageDtoConverter);
        messages = new ArrayList<>();
        user = new User().setUserName("user")
                .setActive(true).setId(2L)
                .setPassword("$2a$10$cAtSqdWyeJOlZbW8bn9BZOyTtWSB0ZzgxQZX7DDnPw3wP92UUNOai")
                .setRoles(Collections.singleton(UserRole.USER));
        message1 = new Message().setUser(user).setTime(Timestamp.valueOf("2020-03-03 15:45:00")).setText("test message").setTag("test").setId(1);
        message2 = new Message().setUser(user).setTime(Timestamp.valueOf("2020-03-07 15:20:00")).setText("test message").setTag("test").setId(2);
        messages.add(message1);
        messages.add(message2);
        messageDtos = new ArrayList<>();
        messageDto1 = new MessageDto().setUserName("user").setTime("2020-03-03 15:45:00.0").setText("test message").setTag("test").setId(1);
        messageDto2 = new MessageDto().setUserName("user").setTime("2020-03-07 15:20:00.0").setText("test message").setTag("test").setId(2);
        messageDtos.add(messageDto1);
        messageDtos.add(messageDto2);
    }

    @Test
    public void findAll() {
        when(messageRepo.findAll()).thenReturn(messages);
        List<MessageDto> result = subj.findAll();
        assertEquals(messageDtos, result);
        verify(messageRepo, times(1)).findAll();
    }

    @Test
    public void save() {
        when(userRepo.findByUserName("user")).thenReturn(user);
        subj.save(messageDto1.setId(null));
        verify(userRepo, times(1)).findByUserName("user");
        verify(messageRepo, times(1)).save(any(Message.class));
    }

    @Test
    public void findByTag() {
        when(messageRepo.findByTag("test")).thenReturn(messages);
        List<MessageDto> result = subj.findByTag("test");
        assertEquals(messageDtos, result);
        verify(messageRepo, times(1)).findByTag("test");
    }
}