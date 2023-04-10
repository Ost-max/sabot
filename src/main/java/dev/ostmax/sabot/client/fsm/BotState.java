package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.TelegramBotClient;

public interface BotState {

    BotState handleCommand(BotContext botContext);

    String getStateId();
}
