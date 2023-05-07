package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Slf4j
public class TelegramBotStateFactory {

    private final ApplicationContext applicationContext;
    private final UserService userService;

    public TelegramBotStateFactory(ApplicationContext applicationContext, UserService userService) {
        this.applicationContext = applicationContext;
        this.userService = userService;
    }


    public BotState getState(BotContext context) {
        log.info("get messages {} hasCallbackQuery {}", context.getMessage(), context.isHasCallbackQuery());
        Optional<User> userTest = userService.findByTelegramId(context.getUserId());
        if (userTest.isEmpty()) {
            return applicationContext.getBean(NewUserState.class);
        } else {
            User user = userTest.get();
            context.setUser(user);
            if (user.isAdmin()) {
                return applicationContext.getBean(AdminState.class);
            }
            //TODO refactor this
            if("/start".equals(context.getMessage()) && user.getName() != null) {
                user.setStateId(null);
                userService.save(user);
                return applicationContext.getBean(CommonUserState.class);
            }
            BotState savedUserState = getSavedUserState(user.getStateId());
            log.info("stateID {}", user.getStateId());

            if (savedUserState != null) {
                log.info("savedUserState {}", savedUserState);
                return savedUserState;
            }
            if (context.getMessage().startsWith("/")) {
                return applicationContext.getBean(CommonUserState.class);
            }
            return applicationContext.getBean(UnknownCommandState.class);
        }
    }

    private BotState getSavedUserState(String stateId) {
        BotState state = null;
        if(stateId == null) {
            return null;
        }
        switch (stateId) {
            case UserRegistrationState.STATE_ID -> state = applicationContext.getBean(UserRegistrationState.class);
            case CommonUserState.STATE_ID -> state = applicationContext.getBean(CommonUserState.class);
            case EventRegistrationChooseTimeState.STATE_ID -> state = applicationContext.getBean(EventRegistrationChooseTimeState.class);
            case EventRegistrationSaveState.STATE_ID -> state = applicationContext.getBean(EventRegistrationSaveState.class);
        }
        return state;
    }


}
