package nextstep.subway.domain.line;

import nextstep.subway.domain.LineStation.LineStation;
import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.common.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Line extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LineName name;

    private LineColor color;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @OneToMany(
            mappedBy = "line",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<LineStation> lineStations = new ArrayList<>();

    @Embedded
    private Sections sections = Sections.empty();

    @Embedded
    private Distance distance;

    protected Line() {
    }

    private Line(String name, String color, int distance) {
        this.name = LineName.of(name);
        this.color = LineColor.of(color);
        this.distance = Distance.of(distance);
    }

    public static Line create(String name, String color, int distance) {
        return new Line(name, color, distance);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public String getColor() {
        return color.getValue();
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public void addSection(Section section) {
        sections.add(section);
        section.setLine(this);
    }

    public Sections getSections() {
        return sections;
    }

    public void setTerminus(Station upStation, Station downStation) {
        validateDuplicateStation(upStation);
        validateDuplicateStation(downStation);

        lineStations.add(LineStation.create(this, upStation));
        lineStations.add(LineStation.create(this, downStation));

        this.upStation = upStation;
        this.downStation = downStation;
    }

    private void validateDuplicateStation(Station station) {
        if (isExistSameStation(station)) {
            throw new IllegalArgumentException("이미 노선에 포함된 지하철역입니다. station : " + station);
        }
    }

    private boolean isExistSameStation(Station station) {
        return lineStations.stream()
                .map(LineStation::getStation)
                .anyMatch(storedStation -> storedStation.equals(station));
    }

    public void modify(String name, String color) {
        this.name = LineName.of(name);
        this.color = LineColor.of(color);
    }
}