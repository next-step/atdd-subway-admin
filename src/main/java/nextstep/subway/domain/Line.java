package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.dto.LineRequest;
import org.hibernate.Hibernate;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @Column(unique = true)
    private String color;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public Line update(LineRequest lineRequest) {
        this.name = lineRequest.getName();
        this.color = lineRequest.getColor();
        return this;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Line line = (Line) o;
        return id != null && Objects.equals(id, line.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
