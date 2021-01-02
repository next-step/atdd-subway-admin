package nextstep.subway.line.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.exception.CustomException;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @Column
    private String color;
    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color, Station upStation, Station downStation, long distance) {
        this(name, color);
        this.sections.addAll(Arrays.asList(
                new Section(this, upStation, downStation, 0L),
                new Section(this, upStation, downStation, distance)));
    }

    private Line(String name, String color) {
        this.name = name;
        this.color = color;
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

    public List<Station> getUpToDownSortedStations() {
        List<Station> stations = new ArrayList<>();
        Station upStation = getEndSection().getUpStation();
        while (upStation != null) {
            stations.add(upStation);
            upStation = findNextUpStation(upStation);
        }
        return stations;
    }

    private Station findNextUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> !section.isEndSection())
                .filter(section -> section.getUpStation() == upStation)
                .findAny()
                .map(Section::getDownStation)
                .orElse(null);
    }

    private Section getEndSection() {
        return sections.stream()
                .filter(Section::isEndSection)
                .findAny()
                .orElseThrow(() -> new CustomException("상/하행선 종점이 존재하지 않습니다."));
    }
}
