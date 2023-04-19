package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.model.Event;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class EventRegistrationSaveState implements BotState {

    public static final String STATE_ID = "EVENT_REGISTRATION_SAVE";
    private final EventService eventService;
    private final UserService userService;
    private final UnknownCommandState unknownCommandState;
    private final CommonUserState commonUserState;


    public EventRegistrationSaveState(EventService eventService, UserService userService, UnknownCommandState unknownCommandState, CommonUserState commonUserState) {
        this.eventService = eventService;
        this.userService = userService;
        this.unknownCommandState = unknownCommandState;
        this.commonUserState = commonUserState;
    }

    @Override
    @Transactional
    public BotState handleCommand(BotContext botContext) {
        if(botContext.isHasCallbackQuery() && botContext.getCallbackQuery().startsWith(BotCommands.SAVE_EVENT)) {
            var params = botContext.getCallbackQuery().split(" ");
            var time = params[1];
            var templateId = params[2];
            var eventId = params[3];
            Event event;
            log.info("time {} template {} eventId {}", time, templateId, eventId);
            if("-".equals(eventId)) {
                log.info("save as new event");
                event = eventService.registerToEvent(Long.parseLong(templateId), botContext.getUser(), LocalDateTime.parse(time));
            } else {
                log.info("update event");
                event = eventService.registerToEvent(Long.parseLong(eventId), botContext.getUser());
            }
            botContext.getUser().setStateId(null);
            userService.save(botContext.getUser());
            log.info("event id {}" , event.getId());
            botContext.sendMessage("Спасибо, Вы успешно зарегистрировалсись: " + event.getName() + " " + event.getDate() + " " + event.getTime());
            botContext.setMessage(BotCommands.START);
            return commonUserState;

        }
        return unknownCommandState;
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }
}
