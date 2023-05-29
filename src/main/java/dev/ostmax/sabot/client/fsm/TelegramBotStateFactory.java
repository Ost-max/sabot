package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.fsm.states.BotState;
import dev.ostmax.sabot.client.fsm.states.NewUserState;
import dev.ostmax.sabot.client.fsm.states.UnknownCommandState;
import dev.ostmax.sabot.client.fsm.states.UserRegistrationState;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
public class TelegramBotStateFactory {

    private final ApplicationContext applicationContext;
    private final UserService userService;

    private final Map<String, BotState> statesByCommandName;

    private final Map<String, BotState> statesByStateId;

    public TelegramBotStateFactory(ApplicationContext applicationContext, UserService userService, @Qualifier("statesByCommandName") Map<String, BotState> statesByCommandName, @Qualifier("statesByStateId") Map<String, BotState> statesByStateId) {
        this.applicationContext = applicationContext;
        this.userService = userService;
        this.statesByCommandName = statesByCommandName;
        this.statesByStateId = statesByStateId;
    }


    public BotState getState(BotContext context) {
        log.info("get messages {} hasCallbackQuery {}", context.getMessage(), context.isHasCallbackQuery());
        Optional<User> userTest = userService.findByTelegramId(context.getUserId());
        if (userTest.isEmpty()) {
            return applicationContext.getBean(NewUserState.class);
        }

        User user = userTest.get();
        context.setUser(user);
        if (!isRegistered(user)) {
            return applicationContext.getBean(UserRegistrationState.class);
        } else {
            BotState command = statesByCommandName.get(context.getMessage().split(" ")[0]);
            if (command != null) {
                log.info("state by command {}", command);
                return command;
            }

            BotState savedUserState = statesByStateId.get(user.getStateId());
            log.info("stateID {}", user.getStateId());

            if (savedUserState != null) {
                log.info("savedUserState {}", savedUserState);
                return savedUserState;
            }

            return applicationContext.getBean(UnknownCommandState.class);
        }
    }

    private boolean isRegistered(User user) {
        return user.getCreatedDate() != null;
    }

}
