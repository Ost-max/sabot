package dev.ostmax.sabot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTemplate {
    @Id
    @GeneratedValue
    private long id;
    @NonNull
    private String name;
    private int demand;
    @NonNull
    private DayOfWeek occursDayOfWeek;
    @NonNull
    private LocalTime occursTime;
    @NonNull
    private Regularity regularity;
    @ManyToOne
    private Unit unit;
    @OneToMany(mappedBy = "template")
    private Set<EventItem> events;

}
