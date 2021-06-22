package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Distance;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Section(Station upStation, Station downStation, Distance distance, Line line) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    private void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    private void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    private void updateDistance(Distance distance) {
        this.distance = distance;
    }

    public void updateUpStation(Section newSection) {
        Distance distance = newSection.distance();
        updateUpStation(newSection.downStation());
        updateDistance(this.distance.minusDistance(distance));
    }

    public void updateDownStation(Section newSection) {
        Distance distance = newSection.distance();
        updateDownStation(newSection.upStation());
        updateDistance(this.distance.minusDistance(distance));
    }

    public void deleteBetweenSection(Section deleteSection) {
        distance = distance.plusDistance(deleteSection.distance());
        this.updateDownStation(deleteSection.downStation());
    }

    public void clearLine() {
        this.line = null;
    }

    public boolean isSameUpStation(Section section) {
        return this.upStation.equals(section.upStation);
    }

    public boolean isSameDownStation(Section section) {
        return this.downStation.equals(section.downStation);
    }

    public Long id() {
        return id;
    }

    public Distance distance() {
        return distance;
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }
}
