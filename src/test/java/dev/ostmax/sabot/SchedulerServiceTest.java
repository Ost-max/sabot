package dev.ostmax.sabot;

import dev.ostmax.sabot.client.MessageClient;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.repository.UnitRepository;
import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.SchedulingServiceImpl;
import dev.ostmax.sabot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.Mockito.verify;

@SpringBootTest(properties = "application-test.properties")
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class SchedulerServiceTest {

    @MockBean
    private MessageClient testClient;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private SchedulingServiceImpl schedulingService;

    @Test
    public void testNotifyUsersAboutRegistrationForEvent() {
        userService.save(User.builder().telegramId(1L).active(true).name("Test user").build());
        var user2 = User.builder().telegramId(2L).active(true).name("Test Registered user").build();
        userService.save(user2);
        var template = eventService.createTemplate("Test template", 2, UnitRepository.DEFAULT_UNIT_ID, DayOfWeek.SUNDAY, LocalTime.now(), Regularity.ONCE_A_WEEK);
        eventService.registerToEvent(template.getId(), user2, LocalDateTime.now().plusMonths(1)); //TODO CHECK in case of fail
        schedulingService.notifyUsersAboutRegistrationForEvent();
        verify(testClient).sendMessage(eq(1L), contains("начинается новый месяц"), any());
    }

    @Test
    public void testNotifyUsersAboutEvent() {
        var user = User.builder().telegramId(3L).active(true).name("Test Registered user").build();
        userService.save(user);
        var tomorrow = LocalDate.now().plusDays(1);
        var template = eventService.createTemplate("Test template", 2, UnitRepository.DEFAULT_UNIT_ID, tomorrow.getDayOfWeek(), LocalTime.of(10, 0), Regularity.ONCE_A_WEEK);
        eventService.registerToEvent(template.getId(), user, tomorrow.atTime(10, 0));
        schedulingService.notifyUsersBeforeEvent();
        verify(testClient).sendMessage(eq(3L), contains("вы записаны"));
    }

}
