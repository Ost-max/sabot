package dev.ostmax.sabot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToMany
    @Singular
    private Collection<User> users;
    @Column
    private LocalTime time;
    @Column
    private LocalDate date;
    @ManyToOne(targetEntity = EventTemplate.class, fetch = FetchType.EAGER)
    private EventTemplate template;

}
