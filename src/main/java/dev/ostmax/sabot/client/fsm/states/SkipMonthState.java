package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommand;
import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Component
public class SkipMonthState implements BotCommand, BotState {

    private final UserService userService;

    public SkipMonthState(UserService userService) {
        this.userService = userService;
    }


    @Override
    public String getCommandName() {
        return BotCommands.SKIP_MONTH;
    }

    @Override
    public BotState getState() {
        return this;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        // TODO handle cases when notification in current month
        botContext.getUser().setSkipPeriod(LocalDate.now().getMonth().plus(1));
        userService.save(botContext.getUser());
        botContext.sendMessage("Спасибо за ответ! В этом месяце больше вас не побеспокою\uD83D\uDE0A");
        return null;
    }

    @Override
    public String getStateId() {
        return null;
    }
}
