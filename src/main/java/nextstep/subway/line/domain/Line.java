package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
public class Line extends BaseEntity {
    @Embedded
    Sections sections = Sections.from(new ArrayList<>());
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    protected Line() {

    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        sections.add(Section.of(this, upStation, downStation, distance));
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

    public List<Station> getStations() {
        return sections.sections()
            .stream()
            .flatMap(e -> Stream.of(e.getUpStation(), e.getDownStation()))
            .distinct()
            .collect(Collectors.toList());
    }

    public List<Long> getStationIds() {
        return sections.sections()
            .stream()
            .flatMap(e -> Stream.of(e.getUpStation(), e.getDownStation()))
            .distinct()
            .map(e -> e.getId())
            .collect(Collectors.toList());
    }

    public List<Long> getUpStationIds() {
        return sections.sections()
            .stream()
            .map(e -> e.getUpStation().getId())
            .collect(Collectors.toList());
    }

    public List<Long> getDownStationIds() {
        return sections.sections()
            .stream()
            .map(e -> e.getDownStation().getId())
            .collect(Collectors.toList());
    }

    public Optional<Section> findSectionByUpStationId(Long upStationId) {
        return sections.sections()
            .stream()
            .filter(e -> e.getUpStation().getId() == upStationId)
            .findFirst();
    }

    public Optional<Section> findSectionByDownStationId(Long downStationId) {
        return sections.sections()
            .stream()
            .filter(e -> e.getDownStation().getId() == downStationId)
            .findFirst();
    }

    public Long getEndUpStationId() {
        List<Long> list = getUpStationIds();
        list.removeAll(getDownStationIds());
        return list.get(0);
    }

    public Long getEndDownStationId() {
        List<Long> list = getDownStationIds();
        list.removeAll(getUpStationIds());
        return list.get(0);
    }

    public List<Section> getSections() {
        return sections.sections();
    }

}
