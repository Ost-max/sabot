package dev.ostmax.sabot.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@Entity
@Table(name = "event_template_user")
@Immutable
public class EventItem {

    public EventItem(Id id, String name, LocalDateTime time, EventTemplate template, User user) {
        this.user = user;
        this.template = template;
        this.name = name;
        this.time = time;
        if(user != null) {
            this.id.userId = user.getId();
        }
        this.id.templateId = template.getId();
        this.id.time = time;
    }
    @Embeddable
    @Data
    public static class Id implements Serializable {
        @Column(name = "user_id")
        protected UUID userId;
        @Column(name = "event_template_id")
        protected long templateId;
        @Column
        protected LocalDateTime time;
    }

    @EmbeddedId
    @Setter(AccessLevel.NONE)
    private Id id = new Id();
    @Column
    private String name;
    @Column(insertable = false,
            updatable = false)
    @NotNull
    private LocalDateTime time;
    @ManyToOne
    @JoinColumn(
            name = "event_template_id",
            insertable = false,
            updatable = false)
    private EventTemplate template;
    @ManyToOne
    @JoinColumn(
            name = "user_id",
            insertable = false,
            updatable = false)
    private User user;
}
