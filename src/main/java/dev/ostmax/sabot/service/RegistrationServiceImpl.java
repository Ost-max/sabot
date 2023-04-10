package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.EventRepository;
import dev.ostmax.sabot.repository.UnitRepository;
import dev.ostmax.sabot.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.UUID;

@Service
@Slf4j
public class RegistrationServiceImpl implements RegistrationService{

    private final UnitRepository unitRepository;
    private final UserRepository userRepository;

    private final EventService eventService;

    public RegistrationServiceImpl(UnitRepository unitRepository, UserRepository userRepository, EventService eventService) {
        this.unitRepository = unitRepository;
        this.userRepository = userRepository;
        this.eventService = eventService;
    }


    @Override
    @Transactional
    public void registerUser(User user, UUID unitId) {
        user.setUnit(unitRepository.findById(unitId).get());
        userRepository.save(user);
    }


    @Override
    @Transactional
    public Collection<User> getUnitParticipants(UUID unitId) {
        return userRepository.findByUnitId(unitId);
    }

}
