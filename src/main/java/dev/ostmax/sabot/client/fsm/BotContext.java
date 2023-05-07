package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.TelegramBotClient;
import dev.ostmax.sabot.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BotContext {

    private TelegramBotClient client;
    private User user;
    private long chatId;
    private Integer messageId;
    private String message;
    private String callbackQuery;
    private String nick;
    private boolean hasCallbackQuery;
    private Long userId;

    public void sendMessage(String text) {
        client.sendMessage(chatId, text);
    }

    public void sendMessage(String text, ReplyKeyboard keyboard) {
        client.sendMessage(chatId, text, keyboard);
    }

    public void sendReplyMessage(String text, ReplyKeyboard keyboard) {
        client.sendMessage(chatId, text, keyboard, messageId);
    }

}
