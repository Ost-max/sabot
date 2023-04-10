package dev.ostmax.sabot.client.fsm;

import org.springframework.stereotype.Component;

@Component
public class UnknownCommandState implements BotState{

    @Override
    public BotState handleCommand(BotContext context) {
        context.sendMessage("Неизвестная команда.");
        return null;
    }

    @Override
    public String getStateId() {
        return null;
    }
}
