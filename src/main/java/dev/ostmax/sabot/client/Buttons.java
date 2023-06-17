package dev.ostmax.sabot.client;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.client.BotCommands.HELP;
import static dev.ostmax.sabot.client.BotCommands.MY_EVENTS;
import static dev.ostmax.sabot.client.BotCommands.SCHEDULE_REPORT;

@Component
public class Buttons {

    private final TelegramClientProperties config;

    public static final InlineKeyboardButton REGISTER_FOR_EVENT = InlineKeyboardButton.builder()
            .text("✍️ Зарегистрироваться на служение")
            .callbackData(BotCommands.REGISTER_FOR_EVENT)
            .build();

    public static final InlineKeyboardButton REGISTER_FOR_EVENT_SHORT = InlineKeyboardButton.builder()
            .text("✍️ Регистрация")
            .callbackData(BotCommands.REGISTER_FOR_EVENT)
            .build();

    public static final InlineKeyboardButton SKIP_MONTH = InlineKeyboardButton.builder()
            .text("❌ Пропущу")
            .callbackData(BotCommands.SKIP_MONTH)
            .build();

    public static final InlineKeyboardButton START = InlineKeyboardButton.builder()
            .text("Главное меню")
            .callbackData(BotCommands.START)
            .build();

    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("ℹ️ Помощь");
    private static final InlineKeyboardButton MY_EVENTS_BUTTON = new InlineKeyboardButton("\uD83D\uDE4F Когда я служу?");
    private static final InlineKeyboardButton MONTH_EVENTS_BUTTON = new InlineKeyboardButton("\uD83D\uDCC5 Расписание на месяц");

    //Admin
    private static final InlineKeyboardButton USER_LIST_BUTTON = new InlineKeyboardButton("Список участников");
    // Common
    private static final InlineKeyboardButton MAIN_MENU_BUTTON = new InlineKeyboardButton("Главное меню");

    public Buttons(TelegramClientProperties config) {
        this.config = config;
    }

    public InlineKeyboardMarkup mainMenu() {
        HELP_BUTTON.setCallbackData(HELP);
        MONTH_EVENTS_BUTTON.setCallbackData(SCHEDULE_REPORT);
        MY_EVENTS_BUTTON.setCallbackData(MY_EVENTS);
        List<List<InlineKeyboardButton>> rowsInLine = List.of(
                List.of(HELP_BUTTON),
                List.of(MONTH_EVENTS_BUTTON),
                List.of(REGISTER_FOR_EVENT),
                List.of(MY_EVENTS_BUTTON)

        );
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);
        return markupInline;
    }

    public static InlineKeyboardMarkup of(List<InlineKeyboardButton> buttons) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(List.of(buttons));
        return markupInline;
    }

    public static InlineKeyboardMarkup of(InlineKeyboardButton button) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(List.of(List.of(button)));
        return markupInline;
    }

    public static ReplyKeyboard reply(List<KeyboardButton> buttons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setKeyboard(List.of(new KeyboardRow(buttons)));
        return replyKeyboardMarkup;
    }

    public static InlineKeyboardMarkup start() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(List.of(List.of(START)));
        return markupInline;
    }

    public static InlineKeyboardMarkup fromTitles(List<String> strings) {
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
