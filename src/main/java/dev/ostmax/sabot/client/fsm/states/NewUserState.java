package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.UserService;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

@Component()
public class NewUserState implements BotState {

    private static final String STATE_NAME = "NEW_USER";

    private static final String FIRST_GREETINGS = "Здраствуйте, {0}! Я бот-админ детского служения. " +
            "C помощью меня можно записаться на служение, посмотреть расписание на следующий месяц, " +
            "кроме того я буду напоминать вам о предстающих событиях в которых вы участвуете. " +
            "Для начала давайте познакомимся. Для продолжения введите ваше ФИО. Например Иванов Иван Иванович:";
    private final UserService userService;

    public NewUserState(UserService userService) {
        this.userService = userService;
    }

    @Override
    public BotState handleCommand(BotContext context) {
        context.setUser(User.builder().telegramId(context.getUserId()).nick(context.getNick()).stateId(UserRegistrationState.STATE_ID).build());
        context.getClient().sendMessage(context.getChatId(), MessageFormat.format(FIRST_GREETINGS, context.getNick()));
        if(userService.findByTelegramId(context.getUserId()).isEmpty()) {
            userService.save(context.getUser());
        }
        return null;
    }

    @Override
    public String getStateId() {
        return STATE_NAME;
    }
}
