package dev.ostmax.sabot;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.MessageClient;
import dev.ostmax.sabot.client.fsm.TelegramBotStateFactory;
import dev.ostmax.sabot.client.fsm.states.EventRegistrationSaveState;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.EventTemplateRepository;
import dev.ostmax.sabot.repository.UnitRepository;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.UserService;
import dev.ostmax.sabot.service.time.DateTimeUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.function.Consumer;

import static java.time.LocalTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "application-test.properties")
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class EventRegistrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private TelegramBotStateFactory stateFactory;
    @MockBean
    private MessageClient testClient;

    @Autowired
    private EventTemplateRepository eventTemplateRepo;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private EventService eventService;

    @Captor
    ArgumentCaptor<InlineKeyboardMarkup> keyboardCaptor;


    @Test
    public void testRegistrationForEvent() {
        eventService.createTemplate("Test event",
                2,
                UnitRepository.DEFAULT_UNIT_ID,
                DayOfWeek.SUNDAY,
                LocalTime.of(10, 0),
                Regularity.ONCE_A_WEEK,
                LocalDate.now().plusDays(100),
                LocalDate.now().minusDays(100));

        registerForEvent("Test event 0/2");
        verify(testClient).sendMessage(anyLong(), startsWith("Спасибо, Вы успешно зарегистрировалсись:"));

        registerForEvent("Test event 1/2");
        verify(testClient, times(2)).sendMessage(anyLong(), startsWith("Спасибо, Вы успешно зарегистрировалсись:"));
        System.out.println(mockingDetails(testClient).printInvocations());
    }


    @Test
    public void testNoFreeEvents() {

        eventService.createTemplate("Test event",
                2,
                UnitRepository.DEFAULT_UNIT_ID,
                DayOfWeek.SUNDAY,
                LocalTime.of(10, 0),
                Regularity.ONCE_A_WEEK,
                LocalDate.now().plusDays(100),
                LocalDate.now().minusDays(100));

        eventService.createTemplate("Test event 2",
                2,
                UnitRepository.DEFAULT_UNIT_ID,
                DayOfWeek.SUNDAY,
                LocalTime.of(10, 0),
                Regularity.ONCE_A_WEEK,
                LocalDate.now().plusDays(100),
                LocalDate.now().minusDays(100));

        registerForEvent("Test event 0/2");
        verify(testClient).sendMessage(anyLong(), startsWith("Спасибо, Вы успешно зарегистрировалсись:"));

        registerForEvent("Test event 1/2", (keyboard) -> {
            System.out.println(keyboard.size());
            assertThat(keyboard).hasSize(2);
        });
        verify(testClient, times(2)).sendMessage(anyLong(), startsWith("Спасибо, Вы успешно зарегистрировалсись:"));

        registerForEvent("Test event 2 0/2", (keyboard) -> {
            System.out.println(keyboard.size());
            assertThat(keyboard).hasSize(1);
        });

        registerForEvent("Test event 2 1/2", (keyboard) -> {
            System.out.println(keyboard.size());
            assertThat(keyboard).hasSize(1);
        });

        var context = createContext();
        var state = stateFactory.getState(context);
        state.handleCommand(context);

        verify(testClient, atLeastOnce()).sendMessage(
                anyLong(),
                LocalDate.now().getDayOfMonth() > 15 ?
                        contains(DateTimeUtils.getFormattedMonthName(LocalDate.now().plusMonths(1))) :
                        contains(DateTimeUtils.getFormattedMonthName(LocalDate.now())),
                keyboardCaptor.capture());

        var keyboard = keyboardCaptor.getValue();
        var nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).getDayOfMonth();
        assertThat(keyboard.getKeyboard().get(0).stream().filter(button -> button.getText().equals("" + nextSunday)).findAny()).isNotPresent();

        /*  chooseDate(context);
         chooseTime(context, "Test event 2 1/2");
        context.getUser().setStateId(EventRegistrationSaveState.STATE_ID);
        userService.save(context.getUser());
        var  state = stateFactory.getState(context);
        state.handleCommand(context);
        System.out.println(mockingDetails(testClient).printInvocations());
        verify(testClient, atLeastOnce()).sendMessage(anyLong(), contains("На выбранную дату свободных служений не осталось"));
        verify(testClient, atLeastOnce()).sendMessage(anyLong(), contains("К сожалению, этот слот уже недоступен. Пожалуйста, выберете другое время"));
*/

     //   verify(testClient, atLeastOnce()).sendMessage(anyLong(), contains("На текущий период свободных служений не осталось"));

    }

    private void registerForEvent(String targetEvent, Consumer<List<List<InlineKeyboardButton>>>... consumers) {
        var context = createContext();
        chooseDate(context);
        var keyBoard = chooseTime(context, targetEvent);
        for(var consumer: consumers) {
            consumer.accept(keyBoard.getKeyboard());
        }
        var  state = stateFactory.getState(context);
        state.handleCommand(context);
    }

    private InlineKeyboardMarkup chooseTime(BotContext context, String targetEvent) {
        verify(testClient, atLeastOnce()).sendMessage(anyLong(), matches("\\d+\\s[a-я]+\\,\\s"), keyboardCaptor.capture());
        var keyboard = keyboardCaptor.getValue();

        // , keyboard.getKeyboard().get(0).get(0).getText());
        System.out.println(keyboard.getKeyboard());
        System.out.println(targetEvent);

        System.out.println("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} " + keyboard);
        context.setCallbackQuery(keyboard.getKeyboard().stream().filter(button -> button.get(0).getText().equals(targetEvent)).map(button -> button.get(0)).findAny().get().getCallbackData());
        context.setMessage(context.getCallbackQuery());
        return keyboard;
    }


    private void chooseDate(BotContext context) {
        var state = stateFactory.getState(context);
        state.handleCommand(context);

        verify(testClient, atLeastOnce()).sendMessage(
                anyLong(),
                LocalDate.now().getDayOfMonth() > 15 ?
                        contains(DateTimeUtils.getFormattedMonthName(LocalDate.now().plusMonths(1))) :
                        contains(DateTimeUtils.getFormattedMonthName(LocalDate.now())),
                keyboardCaptor.capture());

        var keyboard = keyboardCaptor.getValue();
        var nextSunday = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).getDayOfMonth();
        System.out.println("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} " + nextSunday);

        System.out.println("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} " + keyboard.getKeyboard());

        context.setCallbackQuery(keyboard.getKeyboard().get(0).stream().filter(button -> button.getText().equals("" + nextSunday)).findAny().get().getCallbackData());
        context.setMessage(context.getCallbackQuery());
        System.out.println("}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}}} " + context.getCallbackQuery());

        state = stateFactory.getState(context);
        state.handleCommand(context);
    }

    private BotContext createContext() {
        var user = userService.save(User.builder().createdDate(LocalDate.now()).telegramId((long) (1000000 * Math.random())).active(true).name("Test user").build());

        return BotContext.builder()
                .chatId(user.getTelegramId())
                .userId(user.getTelegramId())
                .nick(user.getNick())
                .client(testClient)
                .message(BotCommands.REGISTER_FOR_EVENT)
                .build();
    }

}
