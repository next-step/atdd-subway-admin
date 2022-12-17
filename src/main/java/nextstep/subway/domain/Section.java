package nextstep.subway.domain;

import javax.persistence.*;

@Entity
public class Section extends BaseEntity {
    private static final String INVALID_SECTION_SIZE_MESSAGE = "역과 역 사이의 거리보다 좁은 거리를 입력해주세요.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;
    @OneToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @OneToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private int distance;

    protected Section() {}

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public int getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public boolean hasSameNameStation(Station station) {
        return upStation.compareName(station)
                || downStation.compareName(station);
    }

    public void updateUpStation(Station downStation, int newDistance) {
        validateValidSize(newDistance);
        this.upStation = downStation;
        this.distance -= newDistance;
    }

    public void downStationUpdate(Station upStation, int newDistance) {
        validateValidSize(newDistance);
        this.downStation = upStation;
        this.distance -= newDistance;
    }

    private void validateValidSize(int newDistance) {
        if (this.distance < newDistance) {
            throw new IllegalArgumentException(INVALID_SECTION_SIZE_MESSAGE);
        }
    }

    public boolean equalsUpStation(Long stationId) {
        return upStation.getId().equals(stationId);
    }

    public boolean equalsDownStation(Long stationId) {
        return downStation.getId().equals(stationId);
    }
}
