package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;

    private Integer distance;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color, Integer distance) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.sections = new Sections();
    }

    public static Line of(String name, String color, Integer distance, Long upStationId, Long downStationId, List<Station> stations) {
        Line line = new Line(name, color, distance);
        line.addUpSection(stations, upStationId);
        line.addDownSection(stations, downStationId);
        return line;
    }

    public void update(Line line) {
        if(Objects.nonNull(line.getName())) {
            this.name = line.getName();
        }

        if(Objects.nonNull(line.getColor())) {
            this.color = line.getColor();
        }

        if(Objects.nonNull(line.getDistance())) {
            this.distance = line.getDistance();
        }
    }

    public void addUpSection(List<Station> stations, Long upStationId) {

        if(Objects.isNull(upStationId)) {
            return;
        }

        Station station = stations.stream()
                .filter(s -> s.matchId(upStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행역이 존재하지 않습니다. upStationId = " + upStationId));

        this.sections.addUpSection(Section.create(this, station));
    }
    public void addDownSection(List<Station> stations, Long downStationId) {

        if(Objects.isNull(downStationId)) {
            return;
        }

        Station station = stations.stream()
                .filter(s -> s.matchId(downStationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("하행역이 존재하지 않습니다. upStationId = " + downStationId));

        this.sections.addDownSection(Section.create(this, station));
    }

    public void addSections(List<Station> stations) {
        this.sections.addAll(
                stations.stream()
                .map(s -> Section.create(this, s))
                .collect(toList())
        );
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

    public Integer getDistance() {
        return distance;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Line line = (Line) o;
        return Objects.equals(name, line.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
