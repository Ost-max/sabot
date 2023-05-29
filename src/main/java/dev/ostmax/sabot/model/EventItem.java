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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Immutable;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Builder
@Entity
@Immutable
@Table(name = "event_template_user")
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

    @Override
    public String toString() {
        return "EventItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time=" + time +
                ", template=" + template.getId() + " " + template.getName() +
                ", user=" + user.getId() + " " + user.getName() +
                '}';
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        EventItem eventItem = (EventItem) o;
        return getId() != null && Objects.equals(getId(), eventItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
