package ru.selezneva.sweater.converters;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ru.selezneva.sweater.dto.MessageDto;
import ru.selezneva.sweater.entity.Message;

@Component
public class MessageToMessageDtoConverter implements Converter<Message, MessageDto> {
    @Override
    public MessageDto convert(Message message) {
        return new MessageDto(
                message.getId(),
                message.getText(),
                message.getTag(),
                message.getTime().toString(),
                message.getUser().getUserName());
    }
}
