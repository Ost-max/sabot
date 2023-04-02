package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.Unit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface UnitRepository extends CrudRepository<Unit, UUID> {
}


