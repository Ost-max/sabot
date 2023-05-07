package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.GroupEvent;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface EventService {


    EventTemplate createTemplate(String name,
                                 int demand,
                                 UUID unitId,
                                 DayOfWeek dayOfWeek,
                                 LocalTime occursTime,
                                 Regularity regularity);

    @Transactional
    EventItem registerToEvent(long templateId, User user, LocalDateTime localDateTime);

    @Transactional
    Optional<EventItem> unregister(long templateId, User user, LocalDateTime localDateTime);

    Set<LocalDate> getAllRegularEventDatesForNextPeriod(UUID unitId, LocalDate date, Regularity regularity);

    Map<LocalDateTime, Set<GroupEvent>> getEventsWithParticipantsForConcreteDate(UUID unitId, LocalDate date);

    Set<EventItem> getEventsForConcreteDate(UUID unitId, LocalDate date);

    List<EventItem> getFutureUserEvent(User user);


    // Event with lack of participants, date is optional
}
