package nextstep.subway.line.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import nextstep.subway.advice.exception.SectionBadRequestException;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Section> sections = new ArrayList<>();

    public Line() {
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


    public List<Section> getSections() {
        return sections;
    }

    public void updateLine(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public void createSection(Section section) {
        sections.add(section);
    }

    public void addSection(Station upStation, Station downStation, int distance) {
        addMiddleStation(upStation, downStation, distance); // 가운데에 역을 추가
        addTerminalStation(upStation, downStation, distance); // 종점에 역을 추가
    }

    private void addTerminalStation(Station upStation, Station downStation, int distance) {
        if (isTerminalStation(upStation, downStation, distance)) {
            sections.add(new Section(upStation, downStation, this, distance));
        }
    }

    private void addMiddleStation(Station upStation, Station downStation, int distance) {
        addMiddleDownStation(upStation, downStation, distance); // 가운데 하행 추가
        addMiddleUpStation(upStation, downStation, distance); // 가운데 상행 추가
        sections.add(new Section(upStation, downStation, this, distance));
    }


    private void addMiddleDownStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(section -> !isDuplicateStations(section, upStation, downStation))
                .filter(section -> section.getUpStation().equals(upStation))
                .filter(section -> validateDistance(section, distance))
                .findFirst().ifPresent(section -> {
            section.updateUpStation(downStation);
            section.updateDistance(section.getDistance() - distance);
        });
    }

    private void addMiddleUpStation(Station upStation, Station downStation, int distance) {
        sections.stream()
                .filter(section -> !isDuplicateStations(section, upStation, downStation))
                .filter(section -> section.getDownStation().equals(downStation))
                .filter(section -> validateDistance(section, distance))
                .findFirst().ifPresent(section -> {
            section.updateDownStation(upStation);
            section.updateDistance(section.getDistance() - distance);
        });
    }

    private boolean isTerminalStation(Station upStation, Station downStation, int distance) {
        return sections.stream()
                .filter(section -> !isDuplicateStations(section, upStation, downStation))
                .filter(section -> section.getDownStation().equals(upStation))
                .anyMatch(section -> section.getUpStation().equals(downStation));
    }

    private boolean validateDistance(Section section, int distance) {
        if (section.getDistance() > distance) {
            return true;
        }
        throw new SectionBadRequestException(section.getDistance(), distance);
    }

    private boolean isDuplicateStations(Section section, Station upStation, Station downStation) {
        return section.getUpStation().equals(upStation) && section.getDownStation().equals(downStation);
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
