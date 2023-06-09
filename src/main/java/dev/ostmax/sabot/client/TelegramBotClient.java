package dev.ostmax.sabot.client;

import dev.ostmax.sabot.client.fsm.BotContext;
import dev.ostmax.sabot.client.fsm.TelegramBotStateFactory;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import static dev.ostmax.sabot.client.BotCommands.LIST_OF_COMMANDS;

@Slf4j
@Component
public class TelegramBotClient extends TelegramLongPollingBot {

    private final TelegramClientProperties config;
    private final TelegramBotStateFactory telegramBotStateFactory;

    public TelegramBotClient(TelegramClientProperties config, TelegramBotStateFactory telegramBotStateFactory1) {
        super(config.getToken());
        log.info(config.getToken());
        this.telegramBotStateFactory = telegramBotStateFactory1;
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
            botContextBuilder.message(update.getMessage().getText());
            botContextBuilder.chatId(update.getMessage().getChatId());
            botContextBuilder.userId(update.getMessage().getFrom().getId());
            botContextBuilder.nick(update.getMessage().getFrom().getUserName());
        } else if (update.hasCallbackQuery()) {
            botContextBuilder.hasCallbackQuery(true);
            botContextBuilder.message(update.getCallbackQuery().getData());
            botContextBuilder.callbackQuery(update.getCallbackQuery().getData());
            botContextBuilder.chatId(update.getCallbackQuery().getMessage().getChatId());
            botContextBuilder.userId(update.getCallbackQuery().getFrom().getId());
            botContextBuilder.nick(update.getCallbackQuery().getFrom().getUserName());
        }
        botContextBuilder.client(this);
        return botContextBuilder.build();
    }

    public void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, null, false);
    }

    public void sendMessage(long chatId, String text, ReplyKeyboard replyKeyboard, boolean markdown) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        message.setReplyMarkup(replyKeyboard);
        message.enableMarkdownV2(markdown);
        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
