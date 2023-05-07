package dev.ostmax.sabot.client.web;

import dev.ostmax.sabot.service.EventService;
import dev.ostmax.sabot.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/report")
@Slf4j
public class ReportsController {

    private final EventService eventService;
    private final ReportService reportService;

    public ReportsController(EventService eventService, ReportService reportService) {
        this.eventService = eventService;
        this.reportService = reportService;
    }

    @RequestMapping(value = "/events/month", method = RequestMethod.GET)
    public String monthEvents(Model model, @RequestParam("from") String from) {
        log.info("call to ");
        model.addAttribute("report", reportService.getReportForMonth(LocalDate.parse(from)));
        return "month_events";
    }
}
