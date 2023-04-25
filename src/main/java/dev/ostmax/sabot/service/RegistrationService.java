package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.User;

import java.util.Collection;
import java.util.UUID;

public interface RegistrationService {

    void registerUser(User user, UUID unitId);

    Collection<User> getUnitParticipants(UUID unitId);

}
