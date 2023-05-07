package dev.ostmax.sabot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportColumn {

    private String name;
    private List<ReportRecord> records;

}
