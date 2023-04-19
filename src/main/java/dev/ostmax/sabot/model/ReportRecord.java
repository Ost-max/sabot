package dev.ostmax.sabot.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ReportRecord {

    @NonNull
    private String title;
    private boolean header;
}
