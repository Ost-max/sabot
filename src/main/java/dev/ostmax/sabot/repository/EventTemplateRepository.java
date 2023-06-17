package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.Regularity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface EventTemplateRepository extends CrudRepository<EventTemplate, Long> {

    List<EventTemplate> findAllByUnitId(UUID unitId);

    @Query("""
            select t from EventTemplate t
            where t.unit.id = ?1 and t.beginDate <= ?2  and t.endDate > ?2 and  t.regularity = ?3""")
    Set<EventTemplate> findAllByUnitIdByEffectiveDate(UUID unitId, LocalDate effectiveDate, Regularity regularity);

    @Query("""
            select e from EventTemplate e
            where e.unit.id = ?1 and e.occursDayOfWeek = ?2 and e.beginDate <= CURRENT_DATE and e.endDate > CURRENT_DATE""")
    Set<EventTemplate> findAllByUnitIdAndOccursDayOfWeek(UUID unitId, DayOfWeek dayOfWeek);

    @Query("""
            select e from EventTemplate e
            where e.unit.id = ?1 and e.occursDayOfWeek = ?2 and e.beginDate <= ?3  and e.endDate > ?3""")
    Set<EventTemplate> findAllByUnitIdAndOccursDayOfWeekByEffectiveDate(UUID unitId, DayOfWeek dayOfWeek, LocalDate effectiveDate);
}

