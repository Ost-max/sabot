package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.Buttons;
import org.springframework.stereotype.Component;

import static dev.ostmax.sabot.client.BotCommands.HELP;
import static dev.ostmax.sabot.client.BotCommands.MY_EVENTS;
import static dev.ostmax.sabot.client.BotCommands.REGISTER_FOR_EVENT;
import static dev.ostmax.sabot.client.BotCommands.SCHEDULE_REPORT;
import static dev.ostmax.sabot.client.BotCommands.START;
import static dev.ostmax.sabot.client.BotCommands.UNREGISTER_FROM_EVENT;

@Component
public class CommonUserState implements BotState {

    public final static String STATE_ID = "COMMON_USER";
    private static final String HELP_MSG = "Я бот-админ детского служения. C помощью меня можно записаться на служение," +
            " посмотреть расписание на следующий месяц, кроме того я буду напоминать вам о предстающих событиях " +
            "в которых вы участвуете. По всем вопросам работы бота обращайтесь: https://t.me/Omaximuz";

    private final UnknownCommandState unknownCommandState;
    private final EventRegistrationChooseDatesState eventRegistration;
    private final ScheduleReportState scheduleReportState;
    private final UnregisterFromEventState unregisterFromEventState; //TODO resolve event by command from context
    private final MyEventsReportState myEventsReportState; //TODO resolve event by command from context

    private final Buttons buttons;

    public CommonUserState(UnknownCommandState unknownCommandState, EventRegistrationChooseDatesState eventRegistration, ScheduleReportState scheduleReportState, UnregisterFromEventState unregisterFromEventState, MyEventsReportState myEventsReportState, Buttons buttons) {
        this.unknownCommandState = unknownCommandState;
        this.eventRegistration = eventRegistration;
        this.scheduleReportState = scheduleReportState;
        this.unregisterFromEventState = unregisterFromEventState;
        this.myEventsReportState = myEventsReportState;
        this.buttons = buttons;
    }

    @Override
    public BotState handleCommand(BotContext context) {
        BotState nextState = null;
        switch (context.getMessage().split(" ")[0]) {
            case START -> context.sendMessage("Выберете действие", buttons.mainMenu());
            case HELP -> context.sendMessage(HELP_MSG);
            case REGISTER_FOR_EVENT -> nextState = eventRegistration;
            case SCHEDULE_REPORT -> nextState = scheduleReportState;
            case UNREGISTER_FROM_EVENT -> nextState = unregisterFromEventState;
            case MY_EVENTS -> nextState = myEventsReportState;
            default -> nextState = unknownCommandState;
        }
        return nextState;
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }
}
