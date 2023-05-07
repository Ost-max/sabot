package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.ReportColumn;
import dev.ostmax.sabot.model.ReportRecord;
import dev.ostmax.sabot.model.User;
import dev.ostmax.sabot.service.time.DateTimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService{

    private final EventService eventService;

    public ReportServiceImpl(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public List<ReportColumn> getReportForMonth(LocalDate start) {
        List<LocalDate> currentMonthEvents = new ArrayList<>(eventService.getAllRegularEventDatesForNextPeriod(DEFAULT_UNIT_ID,
                        start,
                        Regularity.ONCE_A_WEEK));
        var result = new ArrayList<ReportColumn>(currentMonthEvents.size());
        currentMonthEvents.sort(Comparator.naturalOrder());
        log.info(currentMonthEvents.toString());
        for(LocalDate date: currentMonthEvents) {
            List<ReportRecord> eventList = eventService.getEventsWithParticipantsForConcreteDate(DEFAULT_UNIT_ID, date).values()
                    .stream()
                    .flatMap(events ->
                     events.stream().flatMap(event ->
                            Stream.concat(Stream.of(new ReportRecord(event.getName() + " " + event.getTime().toLocalTime(), true)),
                                    event.getParticipants().stream().map(user -> new ReportRecord(user.getName()))))).toList();
            result.add(new ReportColumn(date.format(DateTimeUtils.simple_date_month), eventList));
        }
        return result;
    }

    @Override
    public List<EventItem> getUserEvents(User user) {
        return eventService.getFutureUserEvent(user)
                 .stream()
                 .sorted(Comparator.comparing(EventItem::getTime))
                 .collect(Collectors.toList());
    }
}
