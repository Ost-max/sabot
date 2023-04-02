package dev.ostmax.sabot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
public class Unit {

    @Id
    private UUID id;
    private String name;
    @OneToMany
    private List<User> users;
    @OneToMany
    private List<EventTemplate> eventTemplates;

    public Unit(String name) {
        this.name = name;
    }

    public Unit(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
