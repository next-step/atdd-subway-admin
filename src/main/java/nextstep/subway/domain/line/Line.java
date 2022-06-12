package nextstep.subway.domain.line;

import nextstep.subway.domain.LineStation.LineStation;
import nextstep.subway.domain.LineStation.LineStations;
import nextstep.subway.domain.common.BaseEntity;
import nextstep.subway.domain.common.Distance;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.section.Sections;
import nextstep.subway.domain.station.Station;

import javax.persistence.*;
import java.util.Objects;

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

    private Line(LineName name, LineColor color, Distance distance, Station upStation, Station downStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;

        Section section = Section.create(upStation, downStation, distance);
        section.setLine(this);
        sections.addSection(section);

        lineStations.add(LineStation.create(this, upStation));
        lineStations.add(LineStation.create(this, downStation));
    }

    public static Line create(LineName name, LineColor color, Distance distance, Station upStation, Station downStation) {
        return new Line(name, color, distance, upStation, downStation);
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
        section.setLine(this);

        if (Objects.equals(upStation, section.getDownStation())) {
            createUpStationSection(section);
            return;
        }

        if (Objects.equals(downStation, section.getUpStation())) {
            createDownStationSection(section);
            return;
        }

        if (Objects.equals(upStation, section.getUpStation())) {
            divideSection(section, section.getDownStation());
            return;
        }

        if (Objects.equals(downStation, section.getDownStation())) {
            divideSection(section, section.getUpStation());
            return;
        }

        throw new IllegalArgumentException("상행역과 하행역 둘 중 하나라도 포함되어있지 않으면 구간을 추가할 수 없습니다.");
    }

    private void divideSection(Section section, Station section1) {
        sections.addBetweenSection(section);
        lineStations.add(LineStation.create(this, section1));
    }

    private void createDownStationSection(Section section) {
        setDownStation(section.getDownStation());
        sections.addSection(section);
        distance = distance.add(section.getDistance().getValue());
    }

    private void createUpStationSection(Section section) {
        setUpStation(section.getUpStation());
        sections.addSection(section);
        distance = distance.add(section.getDistance().getValue());
    }


    public Sections getSections() {
        return sections;
    }

    private void setUpStation(Station upStation) {
        if (!Objects.equals(this.upStation, upStation)) {
            lineStations.add(LineStation.create(this, upStation));
            this.upStation = upStation;
        }
    }

    private void setDownStation(Station downStation) {
        if (!Objects.equals(this.downStation, downStation)) {
            lineStations.add(LineStation.create(this, downStation));
            this.downStation = downStation;
        }
    }


    public void modify(String name, String color) {
        this.name = LineName.of(name);
        this.color = LineColor.of(color);
    }

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (!Objects.equals(name, line.name)) return false;
        return Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }
}