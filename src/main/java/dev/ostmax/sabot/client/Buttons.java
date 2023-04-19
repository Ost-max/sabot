package dev.ostmax.sabot.client;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.client.BotCommands.HELP;

@Component
public class Buttons {

    private final TelegramClientProperties config;


    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Помощь");
    private static final InlineKeyboardButton MY_EVENTS_BUTTON = new InlineKeyboardButton("Мероприятия где я участвую");
    private static final InlineKeyboardButton REGISTER_FOR_EVENT = new InlineKeyboardButton("Зарегистрироваться на служение");
    private static final InlineKeyboardButton MONTH_EVENTS_BUTTON = new InlineKeyboardButton("Расписание на месяц");

    //Admin
    private static final InlineKeyboardButton USER_LIST_BUTTON = new InlineKeyboardButton("Список участников");
    // Common
    private static final InlineKeyboardButton MAIN_MENU_BUTTON = new InlineKeyboardButton("Главное меню");

    public Buttons(TelegramClientProperties config) {
        this.config = config;
    }

    public InlineKeyboardMarkup inlineMarkup() {
        HELP_BUTTON.setCallbackData(HELP);
        MONTH_EVENTS_BUTTON.setUrl(config.getReportingUrl() + "/report/events/month");
        REGISTER_FOR_EVENT.setCallbackData(BotCommands.REGISTER_FOR_EVENT);

        List<List<InlineKeyboardButton>> rowsInLine = List.of(
                List.of(HELP_BUTTON),
                List.of(MONTH_EVENTS_BUTTON),
                List.of(REGISTER_FOR_EVENT)
        );

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);
        return markupInline;
    }

    public static InlineKeyboardMarkup of(List<String> strings) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(strings.stream().map(text -> {
                  InlineKeyboardButton button = new InlineKeyboardButton(text);
                  button.setCallbackData(text);
                  return List.of(button);
                }).collect(Collectors.toList()));
        return  markupInline;
    }

    public static InlineKeyboardMarkup fromCommands(List<SimpleCommand> commands) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(commands.stream().map(command -> {
            InlineKeyboardButton button = new InlineKeyboardButton(command.getTitle());
            button.setCallbackData(command.getCommand());
            return List.of(button);
        }).collect(Collectors.toList()));
        return  markupInline;
    }

}
