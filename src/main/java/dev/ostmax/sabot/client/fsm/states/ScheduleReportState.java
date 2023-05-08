package dev.ostmax.sabot.client.fsm.states;

import dev.ostmax.sabot.client.BotCommands;
import dev.ostmax.sabot.client.BotContext;
import dev.ostmax.sabot.client.Buttons;
import dev.ostmax.sabot.client.TelegramClientProperties;
import dev.ostmax.sabot.client.BotCommand;
import dev.ostmax.sabot.service.time.DateTimeUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduleReportState implements BotState, BotCommand {

    private final TelegramClientProperties config;

    public final static String STATE_ID = "schedule_report";

    public ScheduleReportState(TelegramClientProperties config) {
        this.config = config;
    }

    @Override
    public BotState handleCommand(BotContext botContext) {
        var requestedDate = LocalDate.now();
        var reportDate = requestedDate.withDayOfMonth(1);
        List<InlineKeyboardButton> reports = new ArrayList<>();
        reports.add(getButtonForDate(reportDate));
        if (requestedDate.getDayOfMonth() > 15) {
            reports.add(getButtonForDate(reportDate.plusMonths(1)));
        }
        botContext.sendMessage("Расписание на месяц", Buttons.of(reports));
        return null;
    }

    private InlineKeyboardButton getButtonForDate(LocalDate date) {
        InlineKeyboardButton monthBtn = new InlineKeyboardButton(DateTimeUtils.getFormattedMonthName(date));
        monthBtn.setUrl(config.getReportingUrl() + "/report/events/month?from=" + date);
        return monthBtn;
    }

    @Override
    public String getStateId() {
        return STATE_ID;
    }

    @Override
    public String getCommandName() {
        return BotCommands.SCHEDULE_REPORT;
    }

    @Override
    public BotState getState() {
        return this;
    }
}
