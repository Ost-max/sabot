package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.repository.UnitRepository;
import dev.ostmax.sabot.service.RegistrationService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class UserRegistrationState implements BotState {


    public static final String STATE_ID = "USER_REGISTRATION";
    private static final String ERROR_NAME_MSG = "Ошибка ввода. ФИО должно состояить из трёх слов разделённых пробелом.";
    private static final String ERROR_PHONE_MSG = "Ошибка ввода. Телефон должен соотвествовать формату: +79200799979 или 89200799979";
    private static final String ENTER_PHONE = "Введите номер телефона по которуму можно с вами связаться. Например +79200799979";
    private static final String ERROR_DATE_MSG = "Ошибка ввода. Не получается распознать дату. Дата должна быть в формате 17.02.1990";

    private static final String ENTER_DATE_OF_BIRTH = "Введите дату рождения. Например 17.02.1990";

    private static final String SUCCESS_MSG = "Спасибо, вы успешно зарегистрированны.";
    private final RegistrationService registrationService;
    private final StartState startState;


    public UserRegistrationState(RegistrationService registrationService, StartState commonUserState) {
        this.registrationService = registrationService;
        this.startState = commonUserState;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        var user = botContext.getUser();
        if(user.getName() == null) {
            if (botContext.getMessage().trim().split(" ").length != 3) {
                botContext.getClient().sendMessage(botContext.getChatId(), ERROR_NAME_MSG);
                return null;
            } else {
                user.setName(botContext.getMessage().trim());
                registrationService.registerUser(user, UnitRepository.DEFAULT_UNIT_ID);
                botContext.getClient().sendMessage(botContext.getChatId(), ENTER_PHONE);
            }
        } else if (user.getPhone() == null) {
            if (botContext.getMessage().trim().matches("^([+]?\\d{1,2}[-\\s]?|)\\d{3}[-\\s]?\\d{3}[-\\s]?\\d{4}$")) {
                user.setPhone(botContext.getMessage().trim());
                registrationService.registerUser(user, UnitRepository.DEFAULT_UNIT_ID);
                botContext.getClient().sendMessage(botContext.getChatId(), ENTER_DATE_OF_BIRTH);
            } else {
                botContext.getClient().sendMessage(botContext.getChatId(), ERROR_PHONE_MSG);
            }
        } else {
            try {
                var dateOfBirt = LocalDate.parse(botContext.getMessage().trim(), DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                var age = LocalDate.now().getYear() - dateOfBirt.getYear();
                if(age < 10 || age > 98) {
                    botContext.getClient().sendMessage(botContext.getChatId(), ERROR_PHONE_MSG);
                } else {
                    user.setActive(true);
                    user.setCreatedDate(LocalDate.now());
                    user.setDateOfBirth(dateOfBirt);
                    user.setStateId(null);
                    registrationService.registerUser(user, UnitRepository.DEFAULT_UNIT_ID);
                    botContext.getClient().sendMessage(botContext.getChatId(), SUCCESS_MSG);
                    botContext.setMessage(BotCommands.START);
                    return startState;
                }
            } catch (DateTimeParseException exception) {
                botContext.getClient().sendMessage(botContext.getChatId(), ERROR_DATE_MSG);
            }
        }
        return null;
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }
}
