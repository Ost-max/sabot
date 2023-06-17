package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.GroupEvent;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.EventRepository;
import dev.ostmax.sabot.repository.EventTemplateRepository;
import dev.ostmax.sabot.repository.UnitRepository;
import dev.ostmax.sabot.service.time.AllSpecificDaysInAMonthQuery;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventTemplateRepository eventTemplateRepository;
    private final EventRepository eventRepository;
    private final UnitRepository unitRepository;

    public EventServiceImpl(EventTemplateRepository eventTemplateRepository, EventRepository eventRepository, UnitRepository unitRepository) {
        this.eventTemplateRepository = eventTemplateRepository;
        this.eventRepository = eventRepository;
        this.unitRepository = unitRepository;
    }

    @Override
    public EventTemplate createTemplate(String name,
                                        int demand,
                                        UUID unitId,
                                        DayOfWeek dayOfWeek,
                                        LocalTime occursTime,
                                        Regularity regularity,
                                        LocalDate endDate,
                                        LocalDate startDate) {
        return this.eventTemplateRepository.save(EventTemplate.builder()
                .name(name)
                .unit(unitRepository.findById(unitId).get())
                .demand(demand)
                .occursDayOfWeek(dayOfWeek)
                .occursTime(occursTime)
                .endDate(endDate)
                .beginDate(startDate)
                .regularity(regularity).build());

    }


    @Override
    public EventItem registerToEvent(long templateId, User user, LocalDateTime localDateTime) {
       Optional<EventTemplate> template = eventTemplateRepository.findById(templateId);
       if(template.isPresent()) {
           EventItem event = EventItem.builder()
                   .name(template.get().getName())
                   .template(template.get())
                   .user(user)
                   .time(localDateTime)
                   .build();
           return this.eventRepository.save(event);
       }
       return null;
    }

    @Override
    public Optional<EventItem> unregister(long templateId, User user, LocalDateTime time) {
        var event = eventRepository.findById(new EventItem.Id(user.getId(), templateId, time));
        event.ifPresent(eventRepository::delete);
        return event;
    }


    @Override
    public Set<LocalDate> getAllRegularEventDatesForNextPeriod(UUID unitId, LocalDate date, Regularity regularity, boolean filterVyEvent) {
        Collection<EventTemplate> templates = eventTemplateRepository.findAllByUnitIdByEffectiveDate(unitId, date, regularity);
        var existEventMap =  getFutureEventsToTemplateMap(templates, date.atStartOfDay(), date.with(lastDayOfMonth()).atTime(23, 59, 0));
        return templates
                .stream()
                .filter(template -> !filterVyEvent || !existEventMap.containsKey(template) || existEventMap.get(template).size() < template.getDemand())
                .map(EventTemplate::getOccursDayOfWeek)
                .flatMap(day -> date.query(new AllSpecificDaysInAMonthQuery(day)).stream())
                .collect(toSet());
    }

    private Map<EventTemplate, Set<EventItem>> getFutureEventsToTemplateMap(Collection<EventTemplate> templates, LocalDateTime startTime,  LocalDateTime endTime) {
        return eventRepository.findEventsByTimeBetweenAndTemplateIdIn(startTime, endTime, templates.stream().map(EventTemplate::getId).collect(toSet()))
                .stream()
                .collect(Collectors.groupingBy(
                        EventItem::getTemplate,
                        Collectors.mapping(
                                event -> event,
                                Collectors.toSet()
                        )
                ));
    }


    @Override
    public Map<LocalDateTime, Set<GroupEvent>> getEventsWithParticipantsForConcreteDate(UUID unitId, LocalDate date, boolean filterByLimit) {
        Collection<EventTemplate> templates = eventTemplateRepository.findAllByUnitIdAndOccursDayOfWeekByEffectiveDate(unitId, date.getDayOfWeek(), date);
        var existEventMap =  getFutureEventsToTemplateMap(templates, date.atStartOfDay(), date.atTime(23, 59, 59));

        var gropEvents = templates.stream().map(template -> {
                    var eventBuilder = GroupEvent.builder();
                    eventBuilder.template(template);
                    eventBuilder.name(template.getName());
                    if (existEventMap.containsKey(template)) {
                        var events = existEventMap.get(template);
                        eventBuilder.time(events.iterator().next().getTime());
                        eventBuilder.participants(events.stream().map(EventItem::getUser).collect(toSet()));
                    } else {
                        eventBuilder.time(LocalDateTime.of(date, template.getOccursTime()));
                    }
                    return eventBuilder.build();
                }
        ).collect(toSet());

        return gropEvents
                .stream()
                .filter(groupEvent -> !filterByLimit || groupEvent.getParticipants().size() < groupEvent.getTemplate().getDemand())
                .collect(Collectors.groupingBy(
                        GroupEvent::getTime,
                        Collectors.mapping(
                                event -> event,
                                Collectors.toSet()
                        )
                ));
    }

    @Override
    public List<EventItem> getFutureUserEvent(User user) {
        return eventRepository.findEventItemByUserIdAndTimeAfter(user.getId(), LocalDateTime.now());
    }

    @Override
    @Transactional
    public Stream<EventItem> getAllEventsForNextMonthByUnitId(UUID unitId) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDate.now().plusMonths(1).with(lastDayOfMonth()).atTime(LocalTime.MAX);
        return eventRepository.findEventsByTimeBetween(start, end, unitId);
    }

    @Override
    @Transactional
    public Set<EventItem> getAllEventsForNextDate(UUID unitId) {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDate.now().plusDays(1).atTime(LocalTime.MAX);
        return eventRepository.findEventsByTimeBetween(start, end, unitId).collect(toSet());
    }

    @Override
    public Set<EventItem> getAllTemplateEventsForConcreteDate(long templateId, LocalDateTime start) {
        return eventRepository.findEventItemByTemplateIdAndTimeEquals(templateId, start);
    }

}
