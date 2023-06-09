package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.Event;
import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface EventService {


    EventTemplate createTemplate(String name,
                                 int demand,
                                 UUID unitId,
                                 DayOfWeek dayOfWeek,
                                 LocalTime occursTime,
                                 Regularity regularity);

    Collection<EventTemplate> getUnitEvents(UUID unitId);

    @Transactional
    Event registerToEvent(long templateId, User user, LocalDateTime localDateTime);

    @Transactional
    Event registerToEvent(long eventId, User user);

    Collection<LocalDate> getAllRegularEventDatesForNextPeriod(UUID unitId, LocalDate date, Regularity regularity);

    Map<LocalTime, Set<Event>> getEventsForConcreteDate(UUID unitId, LocalDate date);

    Collection<Event> getDemandedEvents(UUID unitId, LocalDate date);

    Collection<Event> getEventsForPeriod(UUID unitId, LocalDate date);
    // Event with lack of participants, date is optional
}
