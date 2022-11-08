package nextstep.subway.domain;

import nextstep.subway.message.ExceptionMessage;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String color;

    protected Line() {
    }

    public Line(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return of(null, name, color);
    }

    public static Line of(Long id, String name, String color) {
        validate(name, color);
        return new Line(id, name, color);
    }

    private static void validate(String name, String color) {
        validateNotNullAndNotEmpty(name);
        validateNotNullAndNotEmpty(color);
    }

    private static void validateNotNullAndNotEmpty(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.REQUIRED);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public void update(String name, String color) {
        validate(name, color);
        this.name = name;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) && name.equals(line.name) && color.equals(line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
