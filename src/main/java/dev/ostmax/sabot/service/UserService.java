package dev.ostmax.sabot.service;


import dev.ostmax.sabot.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserService {

    User create(long telegramId, String nick);

    Optional<User> findByTelegramId(long telegramId);

    Collection<User> getAllActiveUsers();

    void save(User user);
}
