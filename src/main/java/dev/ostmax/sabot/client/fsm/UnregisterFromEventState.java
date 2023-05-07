package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.time.DateTimeUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UnregisterFromEventState implements BotState {

    private final EventService eventService;

    public UnregisterFromEventState(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        var params = botContext.getCallbackQuery().split(" ");
        var time = params[1];
        var templateId = params[2];
        var event =  eventService.unregister(Long.parseLong(templateId), botContext.getUser(), LocalDateTime.parse(time));
        event.ifPresent( item -> botContext.sendMessage("Вы отменили участие: " + item.getName() + " - " + DateTimeUtils.formatDateTime(item.getTime())));
        return null;
    }

    @Override
    public String getStateId() {
        return null;
    }
}
