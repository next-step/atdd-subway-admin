package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;

import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {

    public static final int CANNOT_DELETE_SIZE = 1;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String color;


    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)

    private List<Section> sections;

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    private Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = new ArrayList<>();
        this.sections.add(Section.of(this, upStation, downStation, distance, true));
    }

    public static Line of(String name, String color, Station upStation, Station downStation, int distance) {
        return new Line(name, color, upStation, downStation, distance);
    }

    public static Line of(String name, String color) {
        return new Line(name, color);
    }

    public void update(String name, String color) {
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

    public List<Section> getSections() {
        return this.sections;
    }

    public List<Station> getStations() {
        List<Section> orderedSections = getOrderedSections();
        List<Station> stations = new ArrayList<>();
        for (int i = 0; i < orderedSections.size(); i++) {
            stations.add(orderedSections.get(i).getUp());
        }
        stations.add(orderedSections.get(orderedSections.size() - 1).getDown());
        return stations;
    }

    public List<Section> getOrderedSections() {
        List<Section> result = new ArrayList<>();
        Section nextSection = sections.stream().filter(Section::getStart)
                .findFirst()
                .get();
        while (nextSection != null) {
            result.add(nextSection);
            Station down = nextSection.getDown();
            nextSection = sections.stream()
                    .filter(item -> item.getUp() == down)
                    .findFirst().orElse(null);
        }
        return result;
    }


    public void deleteStation(Station station){
        checkDeletable(station);
        Optional<Section> upSection = sections.stream()
                .filter(item -> item.getDown() == (station))
                .findFirst();
        Optional<Section> downSection = sections.stream()
                .filter(item -> item.getUp() == (station))
                .findFirst();
        if (upSection.isPresent() && downSection.isPresent()) {
            downSection.get().mergeUp(upSection.get());
            sections.remove(upSection.get());
            return;
        }
        if (downSection.isPresent()) {
            sections.remove(downSection.get());
            sections.get(0).setStart();
            return;
        }
        upSection.ifPresent(section -> sections.remove(section));
    }

    private void checkDeletable(Station station) {
        if (this.getStations().stream().noneMatch(item -> item == station)) {
            throw new RuntimeException();
        }
        if (sections.size() == CANNOT_DELETE_SIZE) {
            throw new RuntimeException();
        }
    }

}
