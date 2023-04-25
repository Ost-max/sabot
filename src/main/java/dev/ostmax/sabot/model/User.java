package dev.ostmax.sabot.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="bot_user", indexes = @Index(columnList = "telegramId", unique = true))
public class User {

    public User(long telegramId, String nick) {
        this.telegramId = telegramId;
        this.nick = nick;
    }

    @Id
    @GeneratedValue
    private UUID id;
    @Nonnull
    private String name;
    private LocalDate dateOfBirth;
    private String phone;
    private String nick;
    private long telegramId;
    @OneToMany(mappedBy = "user")
    private Set<EventItem> events;
    @ManyToOne
    private Unit unit;
    private String stateId;
    private boolean isAdmin;
    @Column(name = "active", nullable = false, columnDefinition = "bool default true")
    private boolean active;
}
