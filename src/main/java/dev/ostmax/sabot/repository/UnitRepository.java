package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.Unit;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface UnitRepository extends CrudRepository<Unit, UUID> {

    UUID DEFAULT_UNIT_ID = UUID.fromString("47b1d772-fb1c-48e8-ac49-bce2160cfb80");
}


