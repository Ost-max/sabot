package dev.ostmax.sabot.repository;

import dev.ostmax.sabot.model.User;
import org.springframework.data.repository.CrudRepository;

import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
    List<User> findBySkipPeriodNotOrSkipPeriodNullAndActiveTrue(Month skipPeriod);

    Optional<User> findByNick(String userNick);

    Collection<User> findByUnitId(UUID unitId);

    Optional<User> findByTelegramId(long telegramId);

    Collection<User> findAllByActiveIsTrue();
}
