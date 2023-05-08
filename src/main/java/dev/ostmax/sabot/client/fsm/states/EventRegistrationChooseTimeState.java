package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.client.SimpleCommand;
import dev.ostmax.sabot.model.GroupEvent;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;
import static dev.ostmax.sabot.service.time.DateTimeUtils.simple_date_time;

@Component
@Slf4j
public class EventRegistrationChooseTimeState implements BotState {

    private final EventService eventService;
    private final UserService userService;

    public static final String STATE_ID = "CHOOSE_TIME_EVENT_REGISTRATION";

    public EventRegistrationChooseTimeState(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public BotState handleCommand(BotContext botContext) {
        try {
            var timeToEvents = eventService.getEventsWithParticipantsForConcreteDate(DEFAULT_UNIT_ID, LocalDate.parse(botContext.getMessage()));
            botContext.sendMessage("Выберете время и место:");
            for(var entry: timeToEvents.entrySet()) {
                botContext.sendMessage(entry.getKey().format(simple_date_time),
                        Buttons.fromCommands(entry.getValue().stream()
                                .map(this::mapToCommand).collect(Collectors.toList()))
                );
            }
            botContext.getUser().setStateId(EventRegistrationSaveState.STATE_ID);
            userService.save(botContext.getUser());
        } catch (DateTimeParseException ex) {
            botContext.sendMessage("Не могу распознать дату. Попробуйте ещё раз или нажмите вернуться в главное меню.", Buttons.start());
        }
        return null;
    }

    private SimpleCommand mapToCommand(GroupEvent event) {
        var command = BotCommands.SAVE_EVENT +
                " " +
                event.getTime() +
                " " +
                event.getTemplateId();
        log.info(command);
        return new SimpleCommand(event.getName(), command);
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }

}
