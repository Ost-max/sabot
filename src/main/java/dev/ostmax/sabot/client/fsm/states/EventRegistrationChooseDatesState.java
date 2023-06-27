package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.client.BotCommand;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.UserService;
import dev.ostmax.sabot.service.time.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.Optional;
import java.util.stream.Collectors;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;
import static dev.ostmax.sabot.service.time.DateTimeUtils.simple_date_month;
import static java.time.temporal.TemporalAdjusters.firstDayOfMonth;

@Component
@Slf4j
public class EventRegistrationChooseDatesState implements BotState, BotCommand {

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
            var requestedDate = LocalDate.now();
            var dateWerePrinted = printAvailableDatesFor(requestedDate, botContext);
            if (requestedDate.getDayOfMonth() > 15) {
                dateWerePrinted = dateWerePrinted || printAvailableDatesFor(requestedDate.plusMonths(1).with(firstDayOfMonth()), botContext);
            }
            if(!dateWerePrinted) {
                botContext.sendMessage("На текущий период свободных служений не осталось");
            }
            user.setStateId(eventRegistrationChooseTime.getStateId());
            userService.save(user);
        }
        return null;
    }

    private boolean printAvailableDatesFor(LocalDate requestedDate, BotContext botContext) {
      var  availableDates = eventService.getAllRegularEventDatesForNextPeriod(DEFAULT_UNIT_ID, requestedDate, Regularity.ONCE_A_WEEK, true).
                stream().
                sorted().
                map(this::toButton).
              collect(Collectors.toList());

        if (availableDates.size() > 0) {
            botContext.sendMessage(StringUtils.capitalize(DateTimeUtils.getFormattedMonthName(requestedDate)),
                    Buttons.of(availableDates));
            return true;
        }
        return false;
    }

    private InlineKeyboardButton toButton(LocalDate date) {
        InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(date.getDayOfMonth()));
        button.setCallbackData(date.toString());
        return  button;
    }

    @Override
    public String getStateId() {
        return null;
    }

    @Override
    public String getCommandName() {
        return BotCommands.REGISTER_FOR_EVENT;
    }

    @Override
    public BotState getState() {
        return this;
    }
}
