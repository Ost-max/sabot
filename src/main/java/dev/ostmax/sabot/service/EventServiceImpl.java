package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.GroupEvent;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.EventRepository;
import dev.ostmax.sabot.repository.EventTemplateRepository;
import dev.ostmax.sabot.service.time.AllSpecificDaysInAMonthQuery;
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

import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventTemplateRepository eventTemplateRepository;
    private final EventRepository eventRepository;

    public EventServiceImpl(EventTemplateRepository eventTemplateRepository, EventRepository eventRepository) {
        this.eventTemplateRepository = eventTemplateRepository;
        this.eventRepository = eventRepository;
    }

    @Override
    public EventTemplate createTemplate(String name,
                                        int demand,
                                        UUID unitId,
                                        DayOfWeek dayOfWeek,
                                        LocalTime occursTime,
                                        Regularity regularity) {

        return this.eventTemplateRepository.save(EventTemplate.builder()
                .name(name)
                .demand(demand)
                .occursDayOfWeek(dayOfWeek)
                .occursTime(occursTime)
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
    public Set<LocalDate> getAllRegularEventDatesForNextPeriod(UUID unitId, LocalDate date, Regularity regularity) {
        return  eventTemplateRepository
                .findAllByUnitId(unitId)
                .stream()
                .filter(eventTemplate -> regularity.equals(eventTemplate.getRegularity()))
                .map(EventTemplate::getOccursDayOfWeek)
                .flatMap(day -> date.query(new AllSpecificDaysInAMonthQuery(day)).stream())
                .collect(toSet());
    }

    @Override
    public Map<LocalDateTime, Set<GroupEvent>> getEventsWithParticipantsForConcreteDate(UUID unitId, LocalDate date) {
        Collection<EventTemplate> templates = eventTemplateRepository.findAllByUnitIdAndOccursDayOfWeek(unitId, date.getDayOfWeek());

        var existEventMap = eventRepository.findEventsByTimeBetweenAndTemplateIdIn(date.atStartOfDay(), date.atTime(23, 59), templates.stream().map(EventTemplate::getId).collect(toSet()))
                .stream()
                .collect(Collectors.groupingBy(
                        EventItem::getTemplate,
                        Collectors.mapping(
                                event -> event,
                                Collectors.toSet()
                        )
                ));

        var gropEvents = templates.stream().map(template -> {
                    var eventBuilder = GroupEvent.builder();
                    eventBuilder.templateId(template.getId());
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
                .collect(Collectors.groupingBy(
                        event -> event.getTime(),
                        Collectors.mapping(
                                event -> event,
                                Collectors.toSet()
                        )
                ));
    }

    @Override
    public Set<EventItem> getEventsForConcreteDate(UUID unitId, LocalDate date) {
        Set<EventTemplate> templates = eventTemplateRepository.findAllByUnitIdAndOccursDayOfWeek(unitId, date.getDayOfWeek());
        return eventRepository.findEventsByTimeBetweenAndTemplateIdIn(date.atStartOfDay(), date.atTime(23, 59), templates.stream().map(EventTemplate::getId).collect(toSet()));
    }

    @Override
    public List<EventItem> getFutureUserEvent(User user) {
        return eventRepository.findEventItemByUserAndTimeAfter(user, LocalDateTime.now());
    }

}
