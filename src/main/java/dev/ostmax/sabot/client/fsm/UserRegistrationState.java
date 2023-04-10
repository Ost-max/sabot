package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.repository.UnitRepository;
import dev.ostmax.sabot.service.RegistrationService;
import org.springframework.stereotype.Component;

@Component
public class UserRegistrationState implements BotState {


    public static final String STATE_ID = "USER_REGISTRATION";
    private static final String ERROR_MSG = "Ошибка ввода. ФИО должно состояить из трёх слов разделённых пробелом.";
    private static final String SUCCESS_MSG = "Спасибо, вы успешно зарегистрированны.";
    private final RegistrationService registrationService;
    private final CommonUserState commonUserState;


    public UserRegistrationState(RegistrationService registrationService, CommonUserState commonUserState) {
        this.registrationService = registrationService;
        this.commonUserState = commonUserState;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        if (botContext.getMessage().trim().split(" ").length != 3) {
            botContext.getClient().sendMessage(botContext.getChatId(), ERROR_MSG);
            return null;
        } else {
            botContext.getUser().setName(botContext.getMessage().trim());
            botContext.getUser().setStateId(null);
            registrationService.registerUser(botContext.getUser(), UnitRepository.DEFAULT_UNIT_ID);
            botContext.getClient().sendMessage(botContext.getChatId(), SUCCESS_MSG);
            botContext.setMessage(BotCommands.START);
            return commonUserState;
        }
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }
}
