package dev.ostmax.sabot.client;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;


import java.util.Map;

public interface BotCommands {

    String START = "/start";
    String HELP = "/help";
    String REGISTER = "/register";
    String USER_LIST = "/user_list";
    String REGISTER_FOR_EVENT = "/register_for_event";
    String MY_EVENTS = "/my_events";
    String MONTHLY_SCHEDULE = "/MONTHLY_SCHEDULE";

    String SAVE_EVENT = "save_event";

    Map<String, BotCommand> LIST_OF_COMMANDS = Map.of(
            START, new BotCommand(START, "start bot"),
            HELP, new BotCommand(HELP,  "bot info"),
            MY_EVENTS, new BotCommand(MY_EVENTS,  "events where user participates"),
            MONTHLY_SCHEDULE, new BotCommand(MY_EVENTS,  "All events for next month"),
            REGISTER_FOR_EVENT, new BotCommand(REGISTER_FOR_EVENT, "Register for event")
    );

}
