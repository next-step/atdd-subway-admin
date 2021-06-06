package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;
import java.util.function.Predicate;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upstation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        sections.add(new Section(this, upstation, downStation, distance));
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

    public List<Station> getStations() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        List<Station> stations = findStationInSections();
        return new ArrayList<>(stations);
    }

    private List<Station> findStationInSections() {
        List<Station> stations = new ArrayList<>();
        Section firstSection = findFirstSection();
        stations.add(firstSection.getUpStation());
        stations.addAll(findOthersStations(firstSection.getDownStation()));
        return stations;
    }

    private List<Station> findOthersStations(Station downStation) {
        List<Station> stations = new ArrayList<>();
        stations.add(downStation);
        Section nextSection = findSectionInUpStation(downStation);
        while (!Objects.isNull(nextSection)) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionInUpStation(nextSection.getDownStation());
        }
        return new ArrayList<>(stations);
    }

    private Section findFirstSection() {
        return sections.stream()
                .filter(section -> Objects.isNull(findSectionInDownStation(section.getUpStation())))
                .findFirst()
                .orElseThrow(NoSuchElementException::new);
    }

    private Section findSectionInDownStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.getDownStation() == upStation)
                .findFirst()
                .orElse(null);
    }

    private Section findSectionInUpStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.getUpStation() == downStation)
                .findFirst()
                .orElse(null);
    }
}
