package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.Event;
import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.EventRepository;
import dev.ostmax.sabot.repository.EventTemplateRepository;
import dev.ostmax.sabot.service.time.AllSpecificDaysInAMonthQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
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
    public Collection<EventTemplate> getUnitEvents(UUID unitId) {
        return this.eventTemplateRepository.findAllByUnitId(unitId);
    }

    @Override
    public Event registerToEvent(long templateId, User user, LocalDateTime localDateTime) {
       Optional<EventTemplate> template =  eventTemplateRepository.findById(templateId);
       if(template.isPresent()) {
           Event event = Event.builder()
                   .name(template.get().getName())
                   .template(template.get())
                   .users(List.of(user))
                   .time(localDateTime.toLocalTime())
                   .date(localDateTime.toLocalDate())
                   .build();
           return this.eventRepository.save(event);
       }
       return null;
    }


    @Override
    public Event registerToEvent(long eventId, User user) {
        Optional<Event> event = eventRepository.findById(eventId);
        if(event.isPresent()) {
            event.get().getUsers().add(user);
            return this.eventRepository.save(event.get());
        }
        return null;
    }

    @Override
    public Collection<LocalDate> getAllRegularEventDatesForNextPeriod(UUID unitId, LocalDate date, Regularity regularity) {
        return  eventTemplateRepository
                .findAllByUnitId(unitId)
                .stream()
                .filter(eventTemplate -> regularity.equals(eventTemplate.getRegularity()))
                .map(EventTemplate::getOccursDayOfWeek)
                .flatMap(day -> date.query(new AllSpecificDaysInAMonthQuery(day)).stream())
                .collect(toSet());
    }

    @Override
    public Map<LocalTime, Set<Event>> getEventsForConcreteDate(UUID unitId, LocalDate date) {
        Collection<EventTemplate> templates = eventTemplateRepository.findAllByUnitIdAndOccursDayOfWeek(unitId, date.getDayOfWeek());
        Map<EventTemplate, Event> existEventMap = eventRepository.findEventsByDateAndTemplateIdIn(date, templates.stream().map(EventTemplate::getId).collect(toSet()))
                .stream()
                .collect(toMap(Event::getTemplate, event -> event));
        Map<EventTemplate, Event> timeEventMap = templates.stream()
                .filter(tmpl -> !existEventMap.containsKey(tmpl))
                .map( tmpl -> Event.builder()
                        .template(tmpl)
                        .name(tmpl.getName())
                        .date(date)
                        .time(tmpl.getOccursTime()).build())
                .collect(toMap(Event::getTemplate, event -> event));
        existEventMap.putAll(timeEventMap);
        return existEventMap.entrySet()
                .stream()
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().getOccursTime(),
                        Collectors.mapping(
                                Map.Entry::getValue,
                                Collectors.toSet()
                        )
                ));
    }


    public Collection<Event> getDemandedEvents(UUID unitId, LocalDate date) {
        return null;
    }

    @Override
    public Collection<Event> getEventsForPeriod(UUID unitId, LocalDate start) {
        LocalDate end = start.with(TemporalAdjusters.lastDayOfMonth());
        return eventRepository.findEventsByRange(unitId, start, end);
    }

    public Collection<Event> createEventsForNextMount(UUID unitId) {
      /*  Collection<Event> unitEvents = eventTemplateRepository.fi(unitId);
     LocalDate today = LocalDate.now ( zoneId );
        LocalDate firstOfCurrentMonth = today.withDayOfMonth( 1 ) ;
        ZonedDateTime zdt = firstOfCurrentMonth.atStartOfDay ( zoneId );
        int mnth = LocalDateTime.now().getMonth().getValue();
        unitEvents.stream().filter(template ->
           eventRepository.countForCurrentPeriod(template.getId(), mnth) < 1
        ).map(event -> {


        });*/
        return null;
    }



}
