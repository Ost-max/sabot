package dev.ostmax.sabot.client;

import dev.ostmax.sabot.client.fsm.TelegramBotStateFactory;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


import static dev.ostmax.sabot.client.BotCommands.LIST_OF_COMMANDS;

@Slf4j
@Component
public class TelegramBotClient extends TelegramLongPollingBot implements MessageClient{

    private final TelegramClientProperties config;
    private final TelegramBotStateFactory telegramBotStateFactory;

    public TelegramBotClient(TelegramClientProperties config, TelegramBotStateFactory telegramBotStateFactory) {
        super(config.getToken());
        this.telegramBotStateFactory = telegramBotStateFactory;
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS.values().stream().toList(),
                    new BotCommandScopeDefault(),
                    "ru"));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        this.config = config;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
            log.error(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        BotContext context = createBotContext(update);
        for (var state = telegramBotStateFactory.getState(context); state != null; ) {
            state = state.handleCommand(context);
        }
    }

    private BotContext createBotContext(Update update) {
        BotContext.BotContextBuilder botContextBuilder = BotContext.builder();
        if (update.hasMessage() && update.getMessage().hasText()) {
            log.info(update.getMessage().getFrom().toString());
            botContextBuilder.messageId(update.getMessage().getMessageId());
            botContextBuilder.message(update.getMessage().getText());
            botContextBuilder.chatId(update.getMessage().getChatId());
            botContextBuilder.userId(update.getMessage().getFrom().getId());
            if(update.getMessage().getFrom().getUserName() != null) {
                botContextBuilder.nick(update.getMessage().getFrom().getUserName());
            } else {
                botContextBuilder.nick(update.getMessage().getFrom().getFirstName());
            }
        } else if (update.hasCallbackQuery()) {
            botContextBuilder.messageId(update.getCallbackQuery().getMessage().getMessageId());
            botContextBuilder.hasCallbackQuery(true);
            botContextBuilder.message(update.getCallbackQuery().getData());
            botContextBuilder.callbackQuery(update.getCallbackQuery().getData());
            botContextBuilder.chatId(update.getCallbackQuery().getMessage().getChatId());
            botContextBuilder.userId(update.getCallbackQuery().getFrom().getId());
            if(update.getCallbackQuery().getFrom().getUserName() != null) {
                botContextBuilder.nick(update.getCallbackQuery().getFrom().getUserName());
            } else {
                botContextBuilder.nick(update.getCallbackQuery().getFrom().getFirstName());
            }
        }
        botContextBuilder.client(this);
        return botContextBuilder.build();
    }

    public void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    @Override
    public void sendMessage(long chatId, String text, Object params) {
        sendMessage(chatId, text, (ReplyKeyboard) params);
    }

    public void sendMessage(long chatId, String text, ReplyKeyboard replyKeyboard) {
        sendMessage(chatId, text, replyKeyboard, null);
    }
    public void sendMessage(long chatId, String text, ReplyKeyboard replyKeyboard, Integer messageId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyToMessageId(messageId);
        message.setReplyMarkup(replyKeyboard);
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS.values().stream().toList(),
                    new BotCommandScopeDefault(),
                    "ru"));
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }



}
