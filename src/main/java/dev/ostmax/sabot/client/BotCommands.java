package dev.ostmax.sabot.client;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;


import java.util.Map;

public interface BotCommands {


    String START = "/start";
    String HELP = "/help";
    String REGISTER = "/register";
    String USER_LIST = "/user_list";



    Map<String, BotCommand> LIST_OF_COMMANDS = Map.of(
            START, new BotCommand(START, "start bot"),
            HELP, new BotCommand(HELP,  "bot info"),
            REGISTER, new BotCommand(REGISTER, "Register new user"),
            USER_LIST, new BotCommand(USER_LIST, "Get all unit users")
    );

}
