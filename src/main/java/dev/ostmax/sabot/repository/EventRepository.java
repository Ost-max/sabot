package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

public interface EventRepository extends CrudRepository<Event, Long> {

    @Query("select count(event) from Event event where event.date between :start and :end and event.template.unit.id = :unitId")
    Collection<Event> findEventsByRange(@Param("unitId") UUID unitId, @Param("start") LocalDate start, @Param("end") LocalDate endPeriod);

    @Query("select count(event) from Event event where month(event.time)  = :mnth and event.template.id = :templateId")
    int countForCurrentPeriod(@Param("templateId") UUID templateId,  @Param("mnth") int mnth);

    //TODO rewrite to find event by unit id
    Set<Event> findEventsByDateAndTemplateIdIn(LocalDate date, Collection<Long> templates);

    Event findEventByDateAndTemplateId(LocalDate date, UUID templateId);

}
