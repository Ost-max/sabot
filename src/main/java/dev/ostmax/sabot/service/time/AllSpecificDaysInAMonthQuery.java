package dev.ostmax.sabot.service.time;

import lombok.Data;
import lombok.NonNull;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.time.temporal.ValueRange;
import java.util.ArrayList;
import java.util.List;

@Data
public class AllSpecificDaysInAMonthQuery implements TemporalQuery<List<LocalDate>> {

  @NonNull
  private DayOfWeek requestedDay;

    @Override
    public List<LocalDate> queryFrom(TemporalAccessor temporal) {
        if (!(temporal instanceof LocalDate localDate)) {
            throw new DateTimeException("Only LocalDate is supported");
        }
        List<LocalDate> retList = new ArrayList<LocalDate>();
        ValueRange range = localDate.range(ChronoField.DAY_OF_MONTH);
        for (long dayOfMonth = ((LocalDate) temporal).getDayOfMonth(); dayOfMonth <= range.getMaximum(); dayOfMonth++) {
            LocalDate date = localDate.withDayOfMonth((int) dayOfMonth);
            int dayOfWeek = date.get(ChronoField.DAY_OF_WEEK);
            if (dayOfWeek == requestedDay.getValue()) {
                retList.add(date);
            }
        }
        return  retList;
    }
}
