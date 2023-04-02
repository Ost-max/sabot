package dev.ostmax.sabot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Time;
import java.time.DayOfWeek;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;

@Data
@Entity
public class EventTemplate {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private int demand;
    private DayOfWeek occursDayOfWeek;
    private Time occursTime;
    private Regularity regularity;

}
