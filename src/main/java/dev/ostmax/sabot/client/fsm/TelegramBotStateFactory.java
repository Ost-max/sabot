package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
public class TelegramBotStateFactory {


    private final Map<String, BotState> states;
    private final UserService userService;

    public TelegramBotStateFactory(Map<String, BotState> states, UserService userService) {
        this.states = states;
        this.userService = userService;
    }


    public BotState getState(BotContext context) {
        log.info("get messages {} hasCallbackQuery {}", context.getMessage(), context.isHasCallbackQuery());
        Optional<User> userTest = userService.findByTelegramId(context.getUserId());
        if (userTest.isEmpty()) {
            return states.get("newUserState");
        } else {
            User user = userTest.get();
            context.setUser(user);
            if (user.isAdmin()) {
                return states.get("adminState");
            }
            if("/start".equals(context.getMessage())) {
                return states.get("commonUserState");
            }
            BotState savedUserState = getSavedUserState(user.getStateId());
            log.info("stateID {}", user.getStateId());

            if (savedUserState != null) {
                log.info("savedUserState {}", savedUserState);
                return savedUserState;
            }
            if (context.getMessage().startsWith("/")) {
                return states.get("commonUserState");
            }
            return states.get("unknownCommandState");

        }
    }

    private BotState getSavedUserState(String stateId) {
        BotState state = null;
        if(stateId == null) {
            return null;
        }
        switch (stateId) {
            case UserRegistrationState.STATE_ID -> state = states.get("userRegistrationState");
            case CommonUserState.STATE_ID -> state = states.get("commonUserState");
            case EventRegistrationChooseTimeState.STATE_ID -> state = states.get("eventRegistrationChooseTimeState");
            case EventRegistrationSaveState.STATE_ID -> state = states.get("eventRegistrationSaveState");
        }
        return state;
    }


}
