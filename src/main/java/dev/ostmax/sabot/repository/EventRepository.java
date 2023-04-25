package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.EventItem;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

public interface EventRepository extends CrudRepository<EventItem, Long> {

/*
    @Query("select count(event) from Event event where event.date between :start and :end and event.template.unit.id = :unitId")
    Collection<Event> findEventsByRange(@Param("unitId") UUID unitId, @Param("start") LocalDate start, @Param("end") LocalDate endPeriod);

    @Query("select count(event) from Event event where month(event.time)  = :mnth and event.template.id = :templateId")
    int countForCurrentPeriod(@Param("templateId") UUID templateId,  @Param("mnth") int mnth);
*/

    //TODO rewrite to find event by unit id
    Set<EventItem> findEventsByTimeBetweenAndTemplateIdIn(LocalDateTime start, LocalDateTime end, Collection<Long> templates);

/*
    Event findEventByDateAndTemplateId(LocalDate date, UUID templateId);
*/

}
