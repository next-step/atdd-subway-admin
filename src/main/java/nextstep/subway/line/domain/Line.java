package nextstep.subway.line.domain;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Objects;
import nextstep.subway.common.BaseEntity;

import javax.persistence.*;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "deleted = false")
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "color")
    private String color;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        validEmpty(name, color);

        this.name = name;
        this.color = color;
    }


    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.getStations();
    }

    public List<Section> getSections() {
        return sections.getSections();
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


    private void validEmpty(String name, String color) {
        if (Objects.isNull(name) || name.isEmpty()
            || Objects.isNull(color) || color.isEmpty()
        ) {
            throw new InvalidParameterException("빈 값을 입력 할 수 없습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        if (Objects.isNull(id)) {
            return false;
        }

        Line line = (Line) o;
        return id.equals(line.getId()) && name.equals(line.getName());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
