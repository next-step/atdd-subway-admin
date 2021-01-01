package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;
    @Embedded
    private LineSections lineSections;

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        Section upStationSection = new Section(this, upStation, 0, null);
        Section downStationSection = new Section(this, downStation, distance, upStation);
        this.lineSections = new LineSections(upStationSection, downStationSection);
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

    public List<Section> getLineSections() {
        return lineSections.getOrderedSections();
    }

    public List<Station> getStations() {
        return this.lineSections.getStations();
    }

    public void addLineSection(Section newSection) {
        this.lineSections.addSection(newSection);
    }

    public void removeStation(Long stationId) {
        this.lineSections.removeSection(stationId);
    }
}
