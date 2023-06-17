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
import java.util.stream.Stream;

public interface EventService {


    EventTemplate createTemplate(String name,
                                 int demand,
                                 UUID unitId,
                                 DayOfWeek dayOfWeek,
                                 LocalTime occursTime,
                                 Regularity regularity,
                                 LocalDate endDate,
                                 LocalDate startDate);

    @Transactional
    EventItem registerToEvent(long templateId, User user, LocalDateTime localDateTime);

    @Transactional
    Optional<EventItem> unregister(long templateId, User user, LocalDateTime localDateTime);

    @Transactional
    Set<LocalDate> getAllRegularEventDatesForNextPeriod(UUID unitId, LocalDate date, Regularity regularity, boolean filterVyEvent);

    Map<LocalDateTime, Set<GroupEvent>> getEventsWithParticipantsForConcreteDate(UUID unitId, LocalDate date, boolean filterByLimit);

    List<EventItem> getFutureUserEvent(User user);

    @Transactional
    Stream<EventItem> getAllEventsForNextMonthByUnitId(UUID unitId);

    @Transactional
    Set<EventItem> getAllEventsForNextDate(UUID unitId);

    Set<EventItem> getAllTemplateEventsForConcreteDate(long templateId, LocalDateTime time);
}
