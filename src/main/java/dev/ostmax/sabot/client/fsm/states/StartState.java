package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommand;
import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.service.UserService;
import org.springframework.stereotype.Component;

@Component
public class StartState implements BotState, BotCommand {

    private final Buttons buttons;
    private final UserService userService;

    public StartState(Buttons buttons, UserService userService) {
        this.buttons = buttons;
        this.userService = userService;
    }

    @Override
    public String getCommandName() {
        return BotCommands.START;
    }

    @Override
    public BotState getState() {
        return this;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        botContext.getUser().setStateId(null);
        userService.save(botContext.getUser());
        botContext.sendMessage("Выберете действие", buttons.mainMenu());
        return null;
    }

    @Override
    public String getStateId() {
        return StartState.class.getName();
    }
}
