package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
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
    private final StartState startState;


    public EventRegistrationSaveState(EventService eventService, UserService userService, UnknownCommandState unknownCommandState, StartState startState) {
        this.eventService = eventService;
        this.userService = userService;
        this.unknownCommandState = unknownCommandState;
        this.startState = startState;
    }

    @Override
    @Transactional
    public BotState handleCommand(BotContext botContext) {
        if(botContext.isHasCallbackQuery() && botContext.getCallbackQuery().startsWith(BotCommands.SAVE_EVENT)) {
            var params = botContext.getCallbackQuery().split(" ");
            var time = LocalDateTime.parse(params[1]);
            var templateId = Long.parseLong(params[2]) ;
            log.info("time {} template {}", time, templateId);

            var existingEvents = eventService.getAllTemplateEventsForConcreteDate(templateId, time);
            var existingEvent =  existingEvents.size() > 0 ? existingEvents.iterator().next() : null;
            if (existingEvent == null || existingEvent.getTemplate().getDemand() > existingEvents.size()) {
                var event = eventService.registerToEvent(templateId, botContext.getUser(), time);
                botContext.sendMessage("Спасибо, Вы успешно зарегистрировалсись: " + event.getName() + " " + event.getTime().toLocalDate() + " " + event.getTime().toLocalTime());
                botContext.getUser().setStateId(null);
                userService.save(botContext.getUser());
                botContext.setMessage(BotCommands.START);
                return startState;
            } else {
                botContext.sendMessage("К сожалению, этот слот уже недоступен. Пожалуйста, выберете другое время");
                return null;
            }

        }
        return unknownCommandState;
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }
}
