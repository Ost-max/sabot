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
    private final NewUserState newUserState;

    public StartState(Buttons buttons, UserService userService, NewUserState newUserState) {
        this.buttons = buttons;
        this.userService = userService;
        this.newUserState = newUserState;
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
        if(botContext.getUser().getCreatedDate() != null) {
            botContext.getUser().setStateId(null);
            userService.save(botContext.getUser());
        } else {
            return newUserState;
        }
        botContext.sendMessage("Выберете действие", buttons.mainMenu());
        return null;
    }

    @Override
    public String getStateId() {
        return StartState.class.getName();
    }
}
