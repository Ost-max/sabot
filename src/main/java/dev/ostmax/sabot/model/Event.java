package dev.ostmax.sabot.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

@Data
@Entity
public class Event {

    @Id
    @GeneratedValue
    private UUID id;
    @ManyToMany
    private Set<User> participants;
    @Column
    private Timestamp time;
    @ManyToOne
    private EventTemplate template;

}
