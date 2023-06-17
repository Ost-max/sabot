package dev.ostmax.sabot.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class GroupEvent {

    private EventTemplate template;
    private String name;
    @Singular
    private Set<User> participants;
    private LocalDateTime time;

}
