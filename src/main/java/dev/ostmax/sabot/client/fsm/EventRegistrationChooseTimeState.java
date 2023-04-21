package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.client.SimpleCommand;
import dev.ostmax.sabot.model.Event;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.UserService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;

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
            var timeToEvents = eventService.getEventsMapForConcreteDate(DEFAULT_UNIT_ID, LocalDate.parse(botContext.getMessage()));
            botContext.sendMessage("Выберете время и место:");
            for(var entry: timeToEvents.entrySet()) {
                botContext.sendMessage(entry.getKey().toString(),
                        Buttons.fromCommands(entry.getValue().stream().map(this::mapToCommand).collect(Collectors.toList())));
            }
            botContext.getUser().setStateId(EventRegistrationSaveState.STATE_ID);
            userService.save(botContext.getUser());
        } catch (DateTimeParseException ex) {
            botContext.getUser().setStateId(null);
            botContext.sendMessage("Не могу распознать дату. Попробуйте ещё раз или нажмите кнопку вернуться.");
        }
        return null;
    }

    private SimpleCommand mapToCommand(Event event) {
        var command = BotCommands.SAVE_EVENT +
                " " +
                LocalDateTime.of(event.getDate(), event.getTime()) +
                " " +
                event.getTemplate().getId() +
                " " +
                (event.getId() != null ? event.getId() : "-");
        log.info(command);
        return new SimpleCommand(event.getName(), command);
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }

}
