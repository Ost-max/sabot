package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.Event;
import dev.ostmax.sabot.model.Unit;
import dev.ostmax.sabot.model.User;

import java.util.Collection;
import java.util.UUID;

public interface RegistrationService {

    Unit createUnit(String name);

    void registerUser(User user, UUID unitId);

    void removeUser(String userNick, UUID unitId);

    Event registerForEvent(UUID eventId, String userNick);

    void removeFromEvent(UUID eventId, String userNick);

    Collection<User> getUnitParticipants(UUID unitId);

    Collection<User> getFreeUsers(UUID unitId);
}
