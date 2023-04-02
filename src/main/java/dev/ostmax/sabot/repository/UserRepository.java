package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    Optional<User> findByNick(String userNick);

    Collection<User> findByUnitId(UUID unitId);

    Optional<User> findByTelegramId(long telegramId);
}
