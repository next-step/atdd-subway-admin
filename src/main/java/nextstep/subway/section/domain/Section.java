package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.*;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStation_id")
    private Station upStation = new Station();


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStation_id")
    private Station downStation = new Station();

    public Section() {}

    public Section(int distance) {
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(int distance, Long upStationId, Long downStationId) {

    }

    public void toLine(Line line) {
        this.line = line;
        line.getSections().initSections(this);
    }

    public void addLine(Line line) {
        this.line = line;
        line.getSections().addSection(this);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public int getDistance() {
        return distance;
    }
}
