package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.EventTemplate;
import org.springframework.data.repository.CrudRepository;

import java.time.DayOfWeek;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EventTemplateRepository extends CrudRepository<EventTemplate, Long> {

    List<EventTemplate> findAllByUnitId(UUID unitId);

    Set<EventTemplate> findAllByUnitIdAndOccursDayOfWeek(UUID unitId, DayOfWeek dayOfWeek);

}

