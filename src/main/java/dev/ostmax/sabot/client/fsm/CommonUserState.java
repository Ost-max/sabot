package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.Buttons;
import org.springframework.stereotype.Component;

import static dev.ostmax.sabot.client.BotCommands.HELP;
import static dev.ostmax.sabot.client.BotCommands.REGISTER_FOR_EVENT;
import static dev.ostmax.sabot.client.BotCommands.START;

@Component
public class CommonUserState implements BotState {

    public final static String STATE_ID = "COMMON_USER";
    private final UnknownCommandState unknownCommandState;
    private final EventRegistrationChooseDatesState eventRegistration;


    public CommonUserState(UnknownCommandState unknownCommandState, EventRegistrationChooseDatesState eventRegistration) {
        this.unknownCommandState = unknownCommandState;
        this.eventRegistration = eventRegistration;
    }

    @Override
    public BotState handleCommand(BotContext context) {
        BotState nextState = null;
        switch (context.getMessage()) {
            case START -> context.sendMessage("Выберете действие", Buttons.inlineMarkup());
            case HELP -> context.sendMessage("Раздел в разработке...");
            case REGISTER_FOR_EVENT -> nextState = eventRegistration;
            default -> nextState = unknownCommandState;
        }
        return nextState;
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }
}
