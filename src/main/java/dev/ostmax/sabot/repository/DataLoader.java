package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.Unit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class DataLoader implements ApplicationRunner {

    private final UnitRepository unitRepository;
    @Value("${default.unit.id}")
    private UUID defaultUnitId;


    public DataLoader(UnitRepository unitRepository) {
        this.unitRepository = unitRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(String.valueOf(defaultUnitId));
        if(unitRepository.findById(defaultUnitId).isEmpty()) {
            log.info("creating new unit");
            unitRepository.save(new Unit(defaultUnitId, "Детское"));
        }
    }
}
