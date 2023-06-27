package dev.ostmax.sabot.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
public class MonthReport {

    private String month;
    private List<ReportColumn> columns;

}
