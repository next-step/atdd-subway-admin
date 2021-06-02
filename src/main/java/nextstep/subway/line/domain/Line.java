package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.section.domain.Sections;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Sections sections = new Sections();

    @Column(unique = true)
    private String name;
    private String color;

    protected Line() {
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

    public List<Station> getStationInSections() {
        return sections.getAllStations();
    }

    public Section createSection(Station upStation, Station downStation, Long distance) {
        Section section = new Section(this, upStation, downStation, distance);

        if (!sections.isAddable(upStation, downStation, distance)) {
            throw new IllegalStateException("새로운 구간 사이에 이미 역이 있습니다");
        }

        return section;
    }
}
