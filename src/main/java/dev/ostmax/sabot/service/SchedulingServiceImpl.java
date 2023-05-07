package dev.ostmax.sabot.service;

import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.client.TelegramBotClient;
import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.repository.UnitRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Slf4j
public class SchedulingServiceImpl implements SchedulingService {

    private final EventService eventService;
    private final UserService userService;


    private final TelegramBotClient telegramBotClient;
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM HH:mm").withLocale(Locale.of("RU"));
    private static final String NOTIFY_EVENT_MESSAGE = "Уважаемый(ая) {0} вы записаны на служение {1} которое состоится {2}";
    private static final String NOTIFY_REGISTRATION_MESSAGE = "Уважаемый(ая) {0}, начинается новый месяц, не забудьте зарегистрироваться на служение.";


    public SchedulingServiceImpl(EventService eventService, UserService userService, TelegramBotClient telegramBotClient) {
        this.eventService = eventService;
        this.userService = userService;
        this.telegramBotClient = telegramBotClient;
    }

    @Override
    @Scheduled(cron = "0 0 13 * * *")
    @Transactional
    public void notifyUsersBeforeEvent() {
        var queryDate = LocalDate.now().plus(1, ChronoUnit.DAYS);
        log.info("queryDate: " + queryDate.toString());
        Set<EventItem> events = eventService.getEventsForConcreteDate(UnitRepository.DEFAULT_UNIT_ID, queryDate);
        log.info("events: " + events.size());
        events.forEach(event -> {
                    log.info("events {} users {}", event.getName() + " " + event.getTime() + " ", event.getUser());
                    var messageToUser = MessageFormat.format(NOTIFY_EVENT_MESSAGE, event.getUser().getName(), event.getName(),
                            event.getTime().format(dateFormat));
                    log.info("messageToUser {}", messageToUser);
                    this.telegramBotClient.sendMessage(event.getUser().getTelegramId(), messageToUser);
                }
        );
    }

    @Scheduled(cron = "0 0 13 L-2 * *")
    @Override
    @Transactional
    public void notifyUsersAboutRegistrationForEvent() {
        userService.getAllActiveUsers().forEach(user -> {
            var messageToUser = MessageFormat.format(NOTIFY_REGISTRATION_MESSAGE, user.getName());
            log.info("messageToUser {}", messageToUser);
            this.telegramBotClient.sendMessage(user.getTelegramId(), messageToUser, Buttons.of(List.of(Buttons.REGISTER_FOR_EVENT)));
        });
    }

    public void test() {
        log.info("reportCurrentTime: " + LocalDate.now());
    }

}
