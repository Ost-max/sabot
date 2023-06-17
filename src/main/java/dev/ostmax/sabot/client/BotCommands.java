package dev.ostmax.sabot.client;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;


import java.util.Map;

public interface BotCommands {

    String START = "/start";
    String HELP = "/help";
    String SCHEDULE_REPORT = "/schedule_report";
    String REGISTER = "/register";
    String USER_LIST = "/user_list";
    String REGISTER_FOR_EVENT = "/register_for_event";
    String MY_EVENTS = "/my_events";
    String MONTHLY_SCHEDULE = "/MONTHLY_SCHEDULE";
    String UNREGISTER_FROM_EVENT = "/unregister_from_event";

    String SKIP_MONTH = "/skip_month";

    String SAVE_EVENT = "save_event";

    Map<String, BotCommand> LIST_OF_COMMANDS = Map.of(
            START, new BotCommand(START, "Главное меню"),
            HELP, new BotCommand(HELP,  "Помощь/Информация о боте"),
            MY_EVENTS, new BotCommand(MY_EVENTS,  "Когда я служу?"),
            MONTHLY_SCHEDULE, new BotCommand(MY_EVENTS,  "Расписание на месяц"),
            REGISTER_FOR_EVENT, new BotCommand(REGISTER_FOR_EVENT, "Зарегистрироваться на служение")
    );

}
