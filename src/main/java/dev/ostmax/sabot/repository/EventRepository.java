package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public interface EventRepository extends CrudRepository<EventItem, EventItem.Id> {

    @Query("select e from EventItem e join EventTemplate t on t.id = e.template.id where e.time between ?1 and ?2 and t.unit.id = ?3")
    Stream<EventItem> findEventsByTimeBetween(LocalDateTime timeStart, LocalDateTime timeEnd, UUID unitId);

    //TODO rewrite to find event by unit id
    Set<EventItem> findEventsByTimeBetweenAndTemplateIdIn(LocalDateTime start, LocalDateTime end, Collection<Long> templates);

    List<EventItem> findEventItemByUserIdAndTimeAfter(UUID userId, LocalDateTime start);

}
