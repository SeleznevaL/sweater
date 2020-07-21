package ru.selezneva.sweater.service;

import ru.selezneva.sweater.dto.MessageDto;

import java.util.List;

public interface MessageService {
    List<MessageDto> findAll();
    void save(MessageDto messageDto);
    List<MessageDto> findByTag(String tag);
}
