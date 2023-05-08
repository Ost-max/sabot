package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotContext;

public interface BotState {

    BotState handleCommand(BotContext botContext);

    String getStateId();
}
