package nextstep.subway.line.domain;

import static java.util.Collections.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.LineEndpointException;
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

    @OneToMany(mappedBy = "line")
    private List<Section> sections = new LinkedList<>();

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

    public void addSection(Section section) {
        validateSection(sections, section);
        sections.add(section);
    }

    private static void validateSection(List<Section> sections, Section section) {
        if (Objects.isNull(section)) {
            throw new IllegalArgumentException("null 값을 추가할 수 없습니다.");
        }

        if (sections.contains(section)) {
            throw new IllegalArgumentException("이미 추가 된 Section 입니다.");
        }
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station station = getStartStation();

        while (!station.isLast()) {
            stations.add(station);
            station = station.nextStation();
        }

        stations.add(station);

        return unmodifiableList(stations);
    }

    public List<Section> getSections() {
        return unmodifiableList(sections);
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

    private Station getStartStation() {
        return sections.stream()
            .map(Section::getUpStation)
            .filter(Station::isFirst)
            .findAny()
            .orElseThrow(() -> new LineEndpointException("비 정상적인 노선입니다. 출발역이 존재하지 않습니다."));
    }
}
