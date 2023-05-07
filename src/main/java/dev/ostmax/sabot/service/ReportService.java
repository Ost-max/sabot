package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.EventItem;
import dev.ostmax.sabot.model.ReportColumn;
import dev.ostmax.sabot.model.User;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    List<ReportColumn> getReportForMonth(LocalDate start);

    List<EventItem> getUserEvents(User user);
}
