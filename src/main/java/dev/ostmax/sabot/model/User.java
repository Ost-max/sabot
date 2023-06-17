package dev.ostmax.sabot.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@AllArgsConstructor
@Builder
@Table(name="bot_user", indexes = @Index(columnList = "telegramId", unique = true))
public class User {

    public User(long telegramId, String nick) {
        this.telegramId = telegramId;
        this.nick = nick;
    }

    @Id
    @GeneratedValue
    private UUID id;
    @Nonnull
    private String name;
    private LocalDate dateOfBirth;
    private String phone;
    private String nick;
    private long telegramId;
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private Set<EventItem> events;
    @ManyToOne
    private Unit unit;
    private String stateId;
    private boolean isAdmin;
    @Column(name = "active", nullable = false, columnDefinition = "bool default true")
    private boolean active;
    @Column(name = "blocked", nullable = false, columnDefinition = "bool default false")
    private boolean blocked;
    @CreatedDate
    private LocalDate createdDate;
    private Month skipPeriod;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (telegramId != user.telegramId) return false;
        if (isAdmin != user.isAdmin) return false;
        if (active != user.active) return false;
        if (blocked != user.blocked) return false;
        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(name, user.name)) return false;
        if (!Objects.equals(dateOfBirth, user.dateOfBirth)) return false;
        if (!Objects.equals(phone, user.phone)) return false;
        if (!Objects.equals(nick, user.nick)) return false;
        if (!Objects.equals(stateId, user.stateId)) return false;
        if (!Objects.equals(createdDate, user.createdDate)) return false;
        return skipPeriod == user.skipPeriod;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + (phone != null ? phone.hashCode() : 0);
        result = 31 * result + (nick != null ? nick.hashCode() : 0);
        result = 31 * result + (int) (telegramId ^ (telegramId >>> 32));
        result = 31 * result + (stateId != null ? stateId.hashCode() : 0);
        result = 31 * result + (isAdmin ? 1 : 0);
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (blocked ? 1 : 0);
        result = 31 * result + (createdDate != null ? createdDate.hashCode() : 0);
        result = 31 * result + (skipPeriod != null ? skipPeriod.hashCode() : 0);
        return result;
    }
}
