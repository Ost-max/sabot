package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.UserService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component()
public class NewUserState implements BotState {

    private static final String STATE_NAME = "NEW_USER";

    private static final String FIRST_GREETINGS = "Здраствуйте, {0}! Я бот-админ мероприятий. " +
            "C помощью меня можно записаться на мероприятие, посмотреть расписание на следующий месяц, " +
            "кроме того я буду напоминать вам о предстающих событиях в которых вы участвуете." +
            "Для начала давайте познакомимся. Для продолжения введите ваще ФИО. Например Иванов Иван Иванович:";
    private final UserService userService;


    public NewUserState(UserService userService, UserRegistrationState userRegistrationState) {
        this.userService = userService;
    }

    @Override
    public BotState handleCommand(BotContext context) {
        context.setUser(User.builder().telegramId(context.getUserId()).nick(context.getNick()).stateId(UserRegistrationState.STATE_ID).build());
        context.getClient().sendMessage(context.getChatId(), MessageFormat.format(FIRST_GREETINGS, context.getNick()));
        userService.save(context.getUser());
        return null;
    }

    @Override
    public String getStateId() {
        return STATE_NAME;
    }
}
