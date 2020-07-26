package ru.selezneva.sweater.service.impl;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.selezneva.sweater.dto.MessageDto;
import ru.selezneva.sweater.entity.Message;
import ru.selezneva.sweater.repos.MessageRepo;
import ru.selezneva.sweater.repos.UserRepo;
import ru.selezneva.sweater.service.MessageService;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final Converter<Message, MessageDto> messageToMessageDtoConverter;

    public MessageServiceImpl(MessageRepo messageRepo, UserRepo userRepo, Converter<Message, MessageDto> messageToMessageDtoConverter) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.messageToMessageDtoConverter = messageToMessageDtoConverter;
    }

    @Override
    public List<MessageDto> findAll() {
        Iterable<Message> all = messageRepo.findAll();
        List<MessageDto> messages = new ArrayList<>();
        for (Message message : all) {
            messages.add(messageToMessageDtoConverter.convert(message));
        }
        return messages;
    }

    @Override
    @Transactional
    public void save(MessageDto messageDto) {
        messageRepo.save(new Message()
                .setTag(messageDto.getTag())
                .setText(messageDto.getText())
                .setTime(Timestamp.from(Instant.now()))
                .setUser(userRepo.findByUserName(messageDto.getUserName())));
    }

    @Override
    public List<MessageDto> findByTag(String tag) {
        return messageRepo.findByTag(tag).stream().map(messageToMessageDtoConverter::convert).collect(Collectors.toList());
    }
}
