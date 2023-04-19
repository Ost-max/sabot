package dev.ostmax.sabot.service;

import dev.ostmax.sabot.model.ReportRecord;

import java.util.List;
import java.util.Map;

public interface ReportService {

    Map<String, List<ReportRecord>> getReportForCurrentMonth();

}
