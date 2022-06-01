package nextstep.subway.domain.line;

import nextstep.subway.domain.LineStation.LineStation;
import nextstep.subway.domain.LineStation.LineStations;
import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.common.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;

import javax.persistence.*;

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

    @Embedded
    private LineStations lineStations = LineStations.create();

    @Embedded
    private Sections sections = Sections.create();

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
        lineStations.add(LineStation.create(this, upStation));
        lineStations.add(LineStation.create(this, downStation));

        this.upStation = upStation;
        this.downStation = downStation;
    }

    public void modify(String name, String color) {
        this.name = LineName.of(name);
        this.color = LineColor.of(color);
    }
}