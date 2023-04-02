package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.Event;
import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.Regularity;

import java.sql.Date;
import java.sql.Time;
import java.time.DayOfWeek;
import java.util.Collection;
import java.util.UUID;

public interface EventService {

    EventTemplate createTemplate(String name,
                                 int demand,
                                 UUID unitId,
                                 DayOfWeek dayOfWeek,
                                 Time occursTime,
                                 Regularity regularity);

    Collection<EventTemplate> getUnitEvents(UUID unitId);

    Collection<Event> createEventsForNextPeriod(UUID unitId);

    // Event with lack of participants, date is optional
    Collection<Event> getDemandedEvents(UUID unitId, Date date);

    // date is optional
    Collection<Event> getAllEvents(UUID unitId, Date date);



}
