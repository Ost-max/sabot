package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.Event;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EventRepository extends CrudRepository<Event, UUID> {
}
