package dev.ostmax.sabot;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.MessageClient;
import dev.ostmax.sabot.client.fsm.TelegramBotStateFactory;
import dev.ostmax.sabot.client.fsm.states.BotState;
import dev.ostmax.sabot.client.fsm.states.StartState;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "application-test.properties")
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class UserRegistrationTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TelegramBotStateFactory stateFactory;

    private final TestClient testClient = new TestClient();


    @Test
    public void testUnregisteredState() {
        var contextBuilder = BotContext.builder();
        contextBuilder.userId(1L);
        contextBuilder.user(User.builder().telegramId(1L).build());
        contextBuilder.client(testClient);
        contextBuilder.message("/start");

        testClient.setSendMessage((id, text) -> assertThat(text).startsWith("Здраствуйте"));
        var state = stateFactory.getState(contextBuilder.build());
        state.handleCommand(contextBuilder.build());
        assertThat(state.getStateId()).isEqualTo("NEW_USER");

        contextBuilder.message("hi!");
        testClient.setSendMessage((id, text) -> assertThat(text).startsWith("Ошибка"));
        state = stateFactory.getState(contextBuilder.build());
        state.handleCommand(contextBuilder.build());
        assertThat(state.getStateId()).isEqualTo("USER_REGISTRATION");

        contextBuilder.message("/start");
        state = stateFactory.getState(contextBuilder.build());
        var newState = state.handleCommand(contextBuilder.build());
        testClient.setSendMessage((id, text) -> assertThat(text).startsWith("Здраствуйте"));
        newState.handleCommand(contextBuilder.build());
        assertThat(state.getStateId()).isEqualTo(StartState.class.getName());
        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(userRepository.findByTelegramId(1L)).isPresent();
    }

}