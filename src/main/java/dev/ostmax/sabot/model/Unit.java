package dev.ostmax.sabot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Unit {

    @Id
    private UUID id;
    private String name;
    @OneToMany
    @ToString.Exclude
    private List<User> users;
    @OneToMany(mappedBy = "unit")
    @ToString.Exclude
    private List<EventTemplate> eventTemplates;

    public Unit(String name) {
        this.name = name;
    }

    public Unit(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Unit unit = (Unit) o;

        if (!Objects.equals(id, unit.id)) return false;
        return Objects.equals(name, unit.name);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
