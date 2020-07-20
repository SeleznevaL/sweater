package ru.selezneva.sweater.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String text;

    private String tag;

    private Timestamp time;

    @ManyToOne
    private User user;

}
