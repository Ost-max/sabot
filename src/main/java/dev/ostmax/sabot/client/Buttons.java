package dev.ostmax.sabot.client;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static dev.ostmax.sabot.client.BotCommands.HELP;
import static dev.ostmax.sabot.client.BotCommands.REGISTER;
import static dev.ostmax.sabot.client.BotCommands.START;
import static dev.ostmax.sabot.client.BotCommands.USER_LIST;

public class Buttons {

    private static final InlineKeyboardButton HELP_BUTTON = new InlineKeyboardButton("Помощь");
    private static final InlineKeyboardButton REGISTER_BUTTON = new InlineKeyboardButton("Регистрация");
    private static final InlineKeyboardButton USER_LIST_BUTTON = new InlineKeyboardButton("Список участников");

    public static InlineKeyboardMarkup inlineMarkup() {
        HELP_BUTTON.setCallbackData(HELP);
        REGISTER_BUTTON.setCallbackData(REGISTER);
        USER_LIST_BUTTON.setCallbackData(USER_LIST);

        List<List<InlineKeyboardButton>> rowsInLine = List.of(
                List.of(HELP_BUTTON),
                List.of(REGISTER_BUTTON),
                List.of(USER_LIST_BUTTON)
        );

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInLine);

        return markupInline;
    }

}
