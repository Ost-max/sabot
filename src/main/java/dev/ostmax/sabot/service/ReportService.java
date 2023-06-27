package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.model.MonthReport;
import dev.ostmax.sabot.model.User;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    MonthReport getReportForMonth(LocalDate start);

    List<EventItem> getUserEvents(User user);
}
