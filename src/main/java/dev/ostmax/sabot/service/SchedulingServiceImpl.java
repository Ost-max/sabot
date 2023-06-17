package dev.ostmax.sabot.service;

import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.client.MessageClient;
import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.repository.UnitRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SchedulingServiceImpl implements SchedulingService {

    private final EventService eventService;
    private final UserService userService;

    private final MessageClient telegramBotClient;
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM HH:mm").withLocale(Locale.of("RU"));
    private static final String NOTIFY_EVENT_MESSAGE = "Уважаемый(ая) {0} вы записаны на служение {1} которое состоится {2}";
    private static final String NOTIFY_REGISTRATION_MESSAGE = "Уважаемый(ая) {0}, начинается новый месяц, не забудьте зарегистрироваться на служение.";


    public SchedulingServiceImpl(EventService eventService, UserService userService, MessageClient telegramBotClient) {
        this.eventService = eventService;
        this.userService = userService;
        this.telegramBotClient = telegramBotClient;
    }

    @Override
    @Scheduled(cron = "0 0 13 * * *")
    @Transactional
    public void notifyUsersBeforeEvent() {
        Set<EventItem> events = eventService.getAllEventsForNextDate(UnitRepository.DEFAULT_UNIT_ID);
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

    @Override
    @Scheduled(cron = "0 0 13 L-2 * *")
    @Scheduled(cron = "0 0 13 L * *")
    @Transactional
    public void notifyUsersAboutRegistrationForEvent() {
        Set<UUID> userIds = eventService.getAllEventsForNextMonthByUnitId(UnitRepository.DEFAULT_UNIT_ID).map(event -> event.getUser().getId()).collect(Collectors.toSet());
       log.info("Reg users: " + userIds);
       var users=  userService.getAllActiveUsers(LocalDate.now().getMonth().plus(1)).stream().filter(user -> !userIds.contains(user.getId()))
               .peek(user -> {
                 var messageToUser = MessageFormat.format(NOTIFY_REGISTRATION_MESSAGE, user.getName());
                 log.info("messageToUser {}", messageToUser);
                 user.setSkipPeriod(null);
                 this.telegramBotClient.sendMessage(user.getTelegramId(),
                    messageToUser,
                    Buttons.of(List.of(Buttons.SKIP_MONTH, Buttons.REGISTER_FOR_EVENT_SHORT)));
        }).collect(Collectors.toSet());
       userService.save(users);
    }

    public void test() {
        log.info("reportCurrentTime: " + LocalDate.now());
    }

}
