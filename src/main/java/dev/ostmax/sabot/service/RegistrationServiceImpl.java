package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.Event;
import dev.ostmax.sabot.model.Unit;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.EventRepository;
import dev.ostmax.sabot.repository.UnitRepository;
import dev.ostmax.sabot.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.type.descriptor.DateTimeUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService{

    private final UnitRepository unitRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public RegistrationServiceImpl(UnitRepository unitRepository, UserRepository userRepository, EventRepository eventRepository) {
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }


    @Override
    public Unit createUnit(String name) {
        return unitRepository.save(new Unit(name));
    }

    @Override
    @Transactional
    public void registerUser(User user, UUID unitId) {
        user.setUnit(unitRepository.findById(unitId).get());
        userRepository.save(user);
    }

    @Override
    public void removeUser(String userNick, UUID unitId) {
        Unit unit = unitRepository.findById(unitId).get();
        // TODO
    }

    @Override
    public Event registerForEvent(UUID eventId, String userNick) {
        Optional<Event> event = eventRepository.findById(eventId);
        Optional<User> user = userRepository.findByNick(userNick);
        if (event.isEmpty()) {
            log.error("Registration failed Event {} for user {} not found", eventId, userNick);
            return null;
        }
        if (user.isEmpty()) {
            log.error("Registration failed User {} for event {} not found", userNick, eventId);
            return null;
        }
        event.get().getParticipants().add(user.get());
        return eventRepository.save(event.get());
    }

    @Override
    public void removeFromEvent(UUID eventId, String userNick) {
        Optional<User> user = userRepository.findByNick(userNick);
        user.ifPresent(value -> eventRepository.findById(eventId).ifPresent(event -> {
            event.getParticipants().remove(value);
            eventRepository.save(event);
        }));
    }

    @Override
    @Transactional
    public Collection<User> getUnitParticipants(UUID unitId) {
        return userRepository.findByUnitId(unitId);
    }

    @Override
    public Collection<User> getFreeUsers(UUID unitId) {
        // запросить все ивенты со следующего периода заджойнить и заджойнить со свеми юзерами
        Timestamp lastMonth = Timestamp.valueOf(LocalDateTime.now().minusMonths(1));
        return userRepository.findByUnitId(unitId).stream()
                .filter(user -> user.getEvents().stream().noneMatch(event -> event.getTime().after(lastMonth)))
                .collect(Collectors.toSet());
    }
}
