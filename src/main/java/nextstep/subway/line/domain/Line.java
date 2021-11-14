package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections.add(new Section(this, upStation, downStation, distance));
    }

    public void update(Line line) {
        this.name = line.getName();
        this.color = line.getColor();
    }
    
    public void updateSections(List<Section> sections) {
        this.sections = sections;
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

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station upStation = getUpStation();
        stations.add(upStation);

        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> nextSection = sections.stream()
                    .filter(section -> section.isUpStation(finalUpStation))
                    .findFirst();
            if (!nextSection.isPresent()) {
                break;
            }
            upStation = nextSection.get().getDownStation();
            stations.add(upStation);
        }
        return stations;
    }

    private Station getUpStation() {
        Station upStation = sections.get(0).getUpStation();
        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> downStation = sections.stream()
                    .filter(section -> section.isDownStation(finalUpStation))
                    .findFirst();
            if (!downStation.isPresent()) {
                break;
            }
            upStation = downStation.get().getUpStation();
        }
        return upStation;
    }
}
