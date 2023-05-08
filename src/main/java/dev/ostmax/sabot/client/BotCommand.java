package dev.ostmax.sabot.client;

import dev.ostmax.sabot.client.fsm.states.BotState;

public interface BotCommand {

    String getCommandName();

    BotState getState();
}
