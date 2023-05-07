package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.RegistrationService;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;

@Component
public class AdminState implements BotState {


    private final RegistrationService registrationService;

    public AdminState(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        return null;
    }

    @Override
    public String getStateId() {
        return null;
    }


    private void printUserList(long chatId, BotContext botContext) {
        String participants = registrationService.getUnitParticipants(DEFAULT_UNIT_ID).
                stream().
                map(User::getName).
                collect(Collectors.joining(", "));
        botContext.sendMessage(participants);
    }
}
