package ru.selezneva.sweater.dto;

import lombok.*;
import lombok.experimental.Accessors;
import ru.selezneva.sweater.entity.Message;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Integer id;

    private String text;

    private String tag;

    private String time;

    private String userName;
}
