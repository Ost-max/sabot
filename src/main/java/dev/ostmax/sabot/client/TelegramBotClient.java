package dev.ostmax.sabot.client;

import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.RegistrationService;
import dev.ostmax.sabot.service.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
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

import java.text.MessageFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.client.BotCommands.HELP;
import static dev.ostmax.sabot.client.BotCommands.LIST_OF_COMMANDS;
import static dev.ostmax.sabot.client.BotCommands.REGISTER;
import static dev.ostmax.sabot.client.BotCommands.START;
import static dev.ostmax.sabot.client.BotCommands.USER_LIST;

@Slf4j
@Component
public class TelegramBotClient extends TelegramLongPollingBot {

    private static final String FIRST_GREETINGS = "Здраствуйте, {0}! Я бот-админ служений. " +
            "C помощью меня можно записаться на служение";
    private static final String REGISTRATION_STEP1 = "Введите ваше ФИО полностью. Например: Иванов Иван Иванович";
    private static final String REGISTRATION_STEP2 = "Сейчас достпуно только одно служение. Записываю вас на детское.";
    private static final String REGISTRATION_STEP3 = "Готово! Ждём вас в это воскресенье в 10:00 в городке.";


    private final TelegramClientProperties config;
    private final RegistrationService registrationService;
    private final UserService userService;
    @Value("${default.unit.id}")
    private UUID defaultUnitId;


    public TelegramBotClient(TelegramClientProperties config, RegistrationService registrationService, UserService userService) {
        super(config.getToken());
        try {
            this.execute(new SetMyCommands(LIST_OF_COMMANDS.values().stream().toList(),
                    new BotCommandScopeDefault(),
                    "ru"));
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
        this.config = config;
        this.registrationService = registrationService;
        this.userService = userService;
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
        long chatId;
        long userId;
        String messageText;
        String memberName;
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();
            userId = update.getMessage().getFrom().getId();
            memberName = update.getMessage().getFrom().getUserName();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            userId = update.getCallbackQuery().getFrom().getId();
            memberName = update.getCallbackQuery().getFrom().getUserName();
            messageText = update.getCallbackQuery().getData();
        } else {
            return;
        }
        log.info("get messages {} hasCallbackQuery {}", messageText, update.hasCallbackQuery());

        Optional<User> user = userService.findByTelegramId(userId);
        String lastCommand = null;
        if (user.isPresent()) {
            lastCommand = user.get().getLastCommand();
            log.info("last command {}", lastCommand);
        }

        if (messageText.startsWith("/")) {
            switch (messageText) {
                case START -> sendMessage(chatId, MessageFormat.format(FIRST_GREETINGS, memberName), Buttons.inlineMarkup());
                case HELP -> printHelp(chatId);
                case REGISTER -> register(chatId, userId, memberName);
                case USER_LIST -> printUserList(chatId);
                default -> log.error("Unexpected message");
            }
        } else if (REGISTER.equals(lastCommand)) {
            register(chatId, user.get(), messageText);
        } else {
            sendMessage(chatId, "Неизвестная команда. Выберите из списка:", Buttons.inlineMarkup());
        }
    }

    private void printUserList(long chatId) {
        String participants = registrationService.getUnitParticipants(defaultUnitId).
                stream().
                map(User::getName).
                collect(Collectors.joining(", "));
        sendMessage(chatId, participants);
    }

    private void register(long chatId, long telegramId, String memberName) {
        Optional<User> userTest = userService.findByTelegramId(telegramId);
        if (userTest.isPresent()) {
            sendMessage(chatId, "Вы уже зарегистрированны");
        } else {
            User user = userService.create(telegramId, memberName);
            user.setLastCommand(REGISTER);
            userService.save(user);
            sendMessage(chatId, REGISTRATION_STEP1);
        }
    }

    private void register(long chatId, User user, String messageText) {
        if (messageText.split(" ").length < 3) {
            sendMessage(chatId, "Некоректное имя, попробуйте ещё раз.");
        } else {
            user.setName(messageText);
            user.setLastCommand(null);
            registrationService.registerUser(user, defaultUnitId);
            sendMessage(chatId, REGISTRATION_STEP2);
            sendMessage(chatId, REGISTRATION_STEP3);
        }
    }

    private void sendMessage(long chatId, String text) {
        sendMessage(chatId, text, null);
    }

    private void sendMessage(long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        if(replyKeyboard != null) {
            message.setReplyMarkup(replyKeyboard);
        }

        try {
            execute(message);
            log.info("Reply sent");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }

    private void printHelp(long chatId) {
        sendMessage(chatId, "HELP is on the way... =)");
    }


}
