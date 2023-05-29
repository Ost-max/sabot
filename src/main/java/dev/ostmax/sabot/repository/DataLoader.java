package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.EventTemplate;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.Unit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;

@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final UnitRepository unitRepository;
    private final EventTemplateRepository eventTemplateRepository;

    public DataLoader(UnitRepository unitRepository, EventTemplateRepository eventTemplateRepository) {
        this.unitRepository = unitRepository;
        this.eventTemplateRepository = eventTemplateRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<Unit> unit = unitRepository.findById(DEFAULT_UNIT_ID);
        if(unit.isEmpty()) {
            log.info("creating new unit");
            unit = Optional.of(unitRepository.save(new Unit(DEFAULT_UNIT_ID, "Детское")));
        }
        if(eventTemplateRepository.findAllByUnitId(DEFAULT_UNIT_ID).isEmpty()) {
            eventTemplateRepository.save(EventTemplate.builder()
                    .name("Старшая группа")
                    .occursTime(LocalTime.of(10, 0))
                    .occursDayOfWeek(DayOfWeek.SUNDAY)
                    .regularity(Regularity.ONCE_A_WEEK)
                    .unit(unit.get())
                    .demand(2)
                    .build());

            eventTemplateRepository.save(EventTemplate.builder()
                    .name("Старшая группа")
                    .occursTime(LocalTime.of(12, 0))
                    .occursDayOfWeek(DayOfWeek.SUNDAY)
                    .regularity(Regularity.ONCE_A_WEEK)
                    .unit(unit.get())
                    .demand(4)
                    .build());

            eventTemplateRepository.save(EventTemplate.builder()
                    .name("Городок")
                    .occursTime(LocalTime.of(10, 0))
                    .occursDayOfWeek(DayOfWeek.SUNDAY)
                    .regularity(Regularity.ONCE_A_WEEK)
                    .unit(unit.get())
                    .demand(4)
                    .build());

            eventTemplateRepository.save(EventTemplate.builder()
                    .name("Городок")
                    .occursTime(LocalTime.of(12, 0))
                    .occursDayOfWeek(DayOfWeek.SUNDAY)
                    .regularity(Regularity.ONCE_A_WEEK)
                    .unit(unit.get())
                    .demand(2)
                    .build());

            eventTemplateRepository.save(EventTemplate.builder()
                    .name("Ясли")
                    .occursTime(LocalTime.of(10, 0))
                    .occursDayOfWeek(DayOfWeek.SUNDAY)
                    .regularity(Regularity.ONCE_A_WEEK)
                    .unit(unit.get())
                    .demand(2)
                    .build());
        }
    }
}
