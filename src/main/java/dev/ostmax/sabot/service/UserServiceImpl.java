package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User create(long telegramId, String nick) {
        return userRepository.save(new User(telegramId, nick));
    }

    @Override
    public Optional<User> findByTelegramId(long telegramId) {
        return userRepository.findByTelegramId(telegramId);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

}
