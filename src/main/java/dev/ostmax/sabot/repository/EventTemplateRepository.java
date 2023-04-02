package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.EventTemplate;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface EventTemplateRepository  extends CrudRepository<EventTemplate, UUID> {
}

