package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommand;
import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import org.springframework.stereotype.Component;

@Component
public class HelpState implements BotState, BotCommand {

    private static final String HELP_MSG = "Я бот-админ детского служения. C помощью меня можно записаться на служение," +
            " посмотреть расписание на следующий месяц, кроме того я буду напоминать вам о предстающих событиях " +
            "в которых вы участвуете. По всем вопросам работы бота обращайтесь: https://t.me/Omaximuz";


    @Override
    public BotState handleCommand(BotContext botContext) {
        botContext.sendMessage(HELP_MSG);;
        return null;
    }

    @Override
    public String getStateId() {
        return HelpState.class.getName();
    }

    @Override
    public String getCommandName() {
        return BotCommands.HELP;
    }

    @Override
    public BotState getState() {
        return this;
    }
}
