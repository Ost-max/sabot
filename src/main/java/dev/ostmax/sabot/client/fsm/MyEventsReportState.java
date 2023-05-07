package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.service.ReportService;
import dev.ostmax.sabot.service.time.DateTimeUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import static dev.ostmax.sabot.client.Buttons.REGISTER_FOR_EVENT;


@Component
public class MyEventsReportState implements BotState {

    private final ReportService reportService;
    private static final String NO_EVENTS_YET = "Вы ещё не записались ни на одно служение";

    public MyEventsReportState(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        var events = reportService.getUserEvents(botContext.getUser());
        if(events.size() > 0) {
            events.forEach(event -> botContext.sendMessage(format(event), Buttons.of(getUnregisterButton(event))));
        } else {
            botContext.sendMessage(NO_EVENTS_YET, Buttons.of(REGISTER_FOR_EVENT));
        }
        return null;
    }

    private String format(EventItem event) {
        return event.getName() + " - "  + event.getTime().format(DateTimeUtils.simple_date_time);
    }

    private  InlineKeyboardButton getUnregisterButton(EventItem event)  {
       return InlineKeyboardButton.builder()
               .text("Отменить участие")
               .callbackData(BotCommands.UNREGISTER_FROM_EVENT + " " +
                        event.getTime() + " " +
                        event.getTemplate().getId())
               .build();
    }

    @Override
    public String getStateId() {
        return null;
    }
}
