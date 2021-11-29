package nextstep.subway.line.domain;

import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.exception.CannotAddException;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections = new Sections();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    /**
     * 연관관계 편의 메서드
     *
     * @param section
     */
    public void addSection(Section section) {
        this.sections.add(section);
        if (!section.equalsLine(this)) {
            section.toLine(this);
        }
    }

    public void removeSection(Section section) {
        section.removeLine();
        this.sections.remove(section);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
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

    public List<Station> getSortedStations() {
        return sections.getSortedList();
    }

    public boolean containsLineStation(Section section) {
        return this.sections.contains(section);
    }

    public void addSections(Distance distance, Station fromStation, Station toStation) {
        validate(fromStation, toStation);

        sections.updateByFromStation(fromStation, distance, toStation);
        sections.updateByToStation(toStation, distance, fromStation);
        sections.add(Section.create(distance, fromStation, toStation, this));
    }

    private void validate(Station upStation, Station downStation) {
        boolean containUpStation = sections.containsStation(upStation);
        boolean containDownStation = sections.containsStation(downStation);
        if (containUpStation && containDownStation) {
            throw new CannotAddException(Messages.ALREADY_EXISTS_SECTION.getValues());
        }

        if (!containUpStation && !containDownStation) {
            throw new CannotAddException(Messages.NOT_INCLUDE_SECTION.getValues());
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
        Line other = (Line) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
