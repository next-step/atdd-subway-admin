package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {

    private static final int DISTANCE_NONE = 0;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private int sequence;

    @OneToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @OneToOne
    @JoinColumn(name = "down_station_id")
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
        validateDistance(distance);
        return new Section(upStation, downStation, distance);
    }

    private static void validateDistance(int distance) {
        if (distance <= DISTANCE_NONE) {
            throw new IllegalArgumentException("거리 값은 " + DISTANCE_NONE + " 을 초과하는 값이어야 합니다.");
        }
    }

    public void toLine(Line line) {
        this.line = line;
        if (!line.contains(this)) {
            line.add(this);
        }
    }

    public void modifyDistance(int distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    public boolean dividable(Section target) {
        return this.distance > target.distance;
    }

    public void divideDistance(Section element) {
        if (!dividable(element)) {
            throw new IllegalArgumentException("추가하려는 구간의 길이는 기존 구간의 길이보다 작아야 합니다. 구간정보를 확인해주세요");
        }
        this.distance -= element.distance;
    }

    public void modifyUpStation(Station station) {
        this.upStation = station;
        validateStations();
    }

    public void modifyDownStation(Station station) {
        this.downStation = station;
        validateStations();
    }

    private void validateStations() {
        if (upStation.equals(downStation)) {
            throw new IllegalStateException("상행역과 하행역은 동일할 수 없습니다.");
        }
    }

    public void modifySequence(int sequence) {
        this.sequence = sequence;
    }

    public void mergeDownStation(Section section) {
        this.distance += section.distance;
        this.downStation = section.downStation;
    }

    public Long getId() {
        return id;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }
}
