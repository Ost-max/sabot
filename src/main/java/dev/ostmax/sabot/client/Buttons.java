package dev.ostmax.sabot.client;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.client.BotCommands.HELP;
import static dev.ostmax.sabot.client.BotCommands.USER_LIST;

public class Buttons {

    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Помощь");
    private static final InlineKeyboardButton REGISTER_BUTTON = new InlineKeyboardButton("Регистрация");
    private static final InlineKeyboardButton USER_LIST_BUTTON = new InlineKeyboardButton("Список участников");
    private static final InlineKeyboardButton REGISTER_FOR_EVENT = new InlineKeyboardButton("Зарегистрироваться на служение");

    public static InlineKeyboardMarkup inlineMarkup() {
        HELP_BUTTON.setCallbackData(HELP);
        USER_LIST_BUTTON.setCallbackData(USER_LIST);
        REGISTER_FOR_EVENT.setCallbackData(BotCommands.REGISTER_FOR_EVENT);

        List<List<InlineKeyboardButton>> rowsInLine = List.of(
                List.of(HELP_BUTTON),
                List.of(USER_LIST_BUTTON),
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
