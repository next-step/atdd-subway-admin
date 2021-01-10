package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.dto.Sections;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.dto.Distance;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

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

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    /**
     * 노선이 가지고 있는 구간에서 정렬된 지하철 역을 가지고 옵니다.
     * @return
     */
    public List<Station> getStations() {
        return this.sections.getStations();
    }


    public Sections getSections() {
        return sections;
    }

    public void addSections(Station upwardStation, Station downStation, int distance) {
        this.addSections(new Section(this, upwardStation, downStation, new Distance(distance)));
    }

    public void addSections(Station upwardStation, Station downStation, Distance distance) {
        this.addSections(new Section(this, upwardStation, downStation, distance));
    }

    public void addSections(Section section) {
        this.sections.addSection(section);
        section.setLine(this);
    }

    /**
     * 주어진 지하철역으로 구간을 제거합니다.
     * @param stationId
     */
    public void removeSectionsByStation(Long stationId) {
        this.sections.removeSectionByStation(stationId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(id, line.id) &&
                Objects.equals(name, line.name) &&
                Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }

    @Override
    public String toString() {
        return "Line{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
