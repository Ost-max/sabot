package dev.ostmax.sabot;

import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.MessageClient;
import dev.ostmax.sabot.client.fsm.TelegramBotStateFactory;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "application-test.properties")
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class UserRegistrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TelegramBotStateFactory stateFactory;
    @MockBean
    private MessageClient testClient;

    @Test
    public void testUnregisteredState() {
        var user = User.builder().telegramId(4L).nick("Тестовый Юзер").build();
        var context = BotContext.builder()
                .chatId(1L)
                .userId(user.getTelegramId())
                .nick(user.getNick())
                .client(testClient)
                .message("/start")
                .build();

        var state = stateFactory.getState(context);
        state.handleCommand(context);
        assertThat(state.getStateId()).isEqualTo("NEW_USER");
        verify(testClient).sendMessage(eq(1L), startsWith("Здраствуйте, Тестовый Юзер!"));

        context.setMessage("hi!");
        state = stateFactory.getState(context);
        state.handleCommand(context);
        assertThat(state.getStateId()).isEqualTo("USER_REGISTRATION");
        verify(testClient).sendMessage(eq(1L), startsWith("Ошибка ввода. ФИО должно состояить из трёх слов разделённых пробелом."));

        context.setMessage("/my_events");
        state = stateFactory.getState(context);
        state.handleCommand(context);
        assertThat(state.getStateId()).isEqualTo("USER_REGISTRATION");
        verify(testClient, atMost(2)).sendMessage(eq(1L), startsWith("Ошибка ввода. ФИО должно состояить из трёх слов разделённых пробелом."));

        context.setMessage("Иванов Иван Иванович");
        state = stateFactory.getState(context);
        state.handleCommand(context);
        verify(testClient).sendMessage(eq(1L), startsWith("Введите номер телефона по которому можно с вами связаться. Например, +79200799979"));

        context.setMessage("+79210347879");
        state = stateFactory.getState(context);
        state.handleCommand(context);
        verify(testClient).sendMessage(eq(1L), startsWith("Введите дату рождения. Например, 17.02.1990"));

        context.setMessage("15.03.1991");
        state = stateFactory.getState(context);
        state.handleCommand(context);
        verify(testClient).sendMessage(eq(1L), startsWith("Спасибо, вы успешно зарегистрированы"));

        assertThat(userRepository.findAll()).hasSize(1);
        var userFromRepo = userRepository.findByTelegramId(user.getTelegramId());
        assertThat(userFromRepo).isPresent();
        assertThat(userFromRepo.get().getName()).isEqualTo("Иванов Иван Иванович");
        assertThat(userFromRepo.get().getPhone()).isEqualTo("+79210347879");
        assertThat(userFromRepo.get().isActive()).isTrue();
        assertThat(userFromRepo.get().isAdmin()).isFalse();
        assertThat(userFromRepo.get().isBlocked()).isFalse();
        assertThat(userFromRepo.get().getCreatedDate()).isNotNull();
        assertThat(userFromRepo.get().getDateOfBirth()).isEqualTo(LocalDate.of(1991, 3, 15));
    }

}
