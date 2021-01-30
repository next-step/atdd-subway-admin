package nextstep.subway.line.domain;

import java.util.List;
import java.util.Optional;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Line extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    private String color;

    @Embedded
    private Sections sections;

    protected Line() {
    }

    public Line(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Line(String name, String color, Station upStation, Station downStation, int distance) {
        this.name = name;
        this.color = color;
        this.sections = new Sections(this, upStation, downStation, distance);
    }

    public void update(Line line, Station upStation, Station downStation, int distance) {
        this.name = line.getName();
        this.color = line.getColor();
        this.sections.add(new Section(this, upStation, downStation, distance));

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
        return sections.getStations();
    }

    public Optional<Section> getSectionByUpStation(Station downStation) {
        return this.getSections().stream()
            .filter(it -> it.getUpStation() == downStation)
            .findFirst();
    }

    public Optional<Section> getSectionByDownStation(Station downStation) {
        return this.sections.getSectionByDownStation(downStation);
    }

    public Station findAnyUpStation() {
        if (this.getSections() == null || this.getSections().isEmpty()) {
            return null;
        }
        return this.getSections().get(0).getUpStation();
    }

    public void addSection(Section newSection) {
        this.sections.add(newSection);
    }


    public Station findFirstStation() {
        Station firstStation = this.findAnyUpStation();
        while (firstStation != null) {
            Optional<Section> nextLineStation = this.getSectionByDownStation(firstStation);
            if (!nextLineStation.isPresent()) {
                break;
            }
            firstStation = nextLineStation.get().getUpStation();
        }

        return firstStation;
    }
}
