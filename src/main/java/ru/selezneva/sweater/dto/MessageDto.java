package ru.selezneva.sweater.dto;

import lombok.Getter;
import lombok.Setter;
import ru.selezneva.sweater.domain.Message;

@Getter
@Setter
public class MessageDto {
    private String text;

    private String tag;

    private String time;

    private String userName;

    public MessageDto(Message message) {
        this.text = message.getText();
        this.tag = message.getTag();
        this.time = message.getTime().toString();
        this.userName = message.getUser().getUserName();
    }
}
