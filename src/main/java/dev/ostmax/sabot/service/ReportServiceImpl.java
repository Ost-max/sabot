package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.Regularity;
import dev.ostmax.sabot.model.ReportRecord;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.ostmax.sabot.repository.UnitRepository.DEFAULT_UNIT_ID;

@Service
public class ReportServiceImpl implements ReportService{

    private final EventService eventService;

    public ReportServiceImpl(EventService eventService) {
        this.eventService = eventService;
    }

    @Override
    public Map<String, List<ReportRecord>> getReportForCurrentMonth() {
        Collection<LocalDate> currentMonthEvents = eventService.getAllRegularEventDatesForNextPeriod(DEFAULT_UNIT_ID,
                LocalDate.now().withDayOfMonth(1),
                Regularity.ONCE_A_WEEK);
        var result = new HashMap<String, List<ReportRecord>> ();
        for(LocalDate date: currentMonthEvents) {
            List<ReportRecord> eventList = eventService.getEventsWithParticipantsForConcreteDate(DEFAULT_UNIT_ID, date).values().stream().flatMap(events ->
                    events.stream().flatMap(event ->
                            Stream.concat(Stream.of(new ReportRecord(event.getName() + " " + event.getTime().toLocalTime(), true)),
                                    event.getParticipants().stream().map(user -> new ReportRecord(user.getName()))))).toList();
            result.put(date.format(DateTimeFormatter.ofPattern("dd MMMM").withLocale(Locale.of("RU"))), eventList);
        }
        return result;
    }
}
