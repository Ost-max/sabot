package dev.ostmax.sabot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@Table(name="bot_user")
public class User {

    public User(long telegramId, String nick) {
        this.telegramId = telegramId;
        this.nick = nick;
    }

    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String nick;

    // create index
    private long telegramId;
    @ManyToMany
    private Collection<Event> events;
    @ManyToOne
    private Unit unit;

    private String lastCommand;
}
