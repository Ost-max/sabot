package dev.ostmax.sabot.client.fsm;

import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.UserService;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;

@Component
public class EventRegistrationChooseDatesState implements BotState {

    private final UserService userService;
    private final EventService eventService;
    private final EventRegistrationChooseTimeState eventRegistrationChooseTime;


    public EventRegistrationChooseDatesState(UserService userService, EventService eventService, EventRegistrationChooseTimeState eventRegistrationChooseTime) {
        this.userService = userService;
        this.eventService = eventService;
        this.eventRegistrationChooseTime = eventRegistrationChooseTime;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        Optional<User> userTest = userService.findByTelegramId(botContext.getUserId());
        if (userTest.isPresent()) {
            User user = userTest.get();
            List<String> availableDates = eventService.getAllRegularEventDatesForNextPeriod(DEFAULT_UNIT_ID, LocalDate.now(), Regularity.ONCE_A_WEEK).
                    stream().
                    map(LocalDate::toString).
                    collect(Collectors.toList());
            botContext.sendMessage("Выберете дату:", Buttons.fromTitles(availableDates));
            user.setStateId(eventRegistrationChooseTime.getStateId());
            userService.save(user);
        }
        return null;
    }

    @Override
    public String getStateId() {
        return null;
    }
}
