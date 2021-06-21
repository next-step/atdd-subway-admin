package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_down_station"))
    private Station downStation;

    private int distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, distance);
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    public void changeUpStation(Station station) {
        this.upStation = station;
    }

    public void changeDownStation(Station station) {
        this.downStation = station;
    }

    public boolean equalUpStation(Section section) {
        return this.upStation.equals(section.getUpStation());
    }

    public void validateSectionDistance(Section newSection) {
        if (newSection.getDistance() >= this.distance) {
            throw new IllegalArgumentException("기존 역 사이보다 크거나 같아 등록할 수 없습니다.");
        }
    }

    public void calculateDistance(Section newSection) {
        this.distance -= newSection.getDistance();
    }

    public void changeStationInMiddle(Section newSection) {
        validateSectionDistance(newSection);
        changeUpStation(newSection.getDownStation());
        calculateDistance(newSection);
    }

    public boolean isStationInSection(Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public void removeLine() {
        this.line = null;
    }

    @Override
    public int compareTo(Section o) {
        if (this.getDownStation().equals(o.getUpStation())) {
            return -1;
        }
        if (this.equals(o)) {
            return 0;
        }
        return 1;
    }
}
