package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.Buttons;
import org.springframework.stereotype.Component;

@Component
public class UnknownCommandState implements BotState{

    @Override
    public BotState handleCommand(BotContext context) {
        context.sendMessage("Неизвестная команда.", Buttons.start());
        return null;
    }

    @Override
    public String getStateId() {
        return null;
    }
}
