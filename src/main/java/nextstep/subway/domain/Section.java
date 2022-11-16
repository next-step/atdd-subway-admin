package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {
    private static final String ERROR_MESSAGE_NOT_NULL_UP_STATION = "상행선은 필수입니다.";
    private static final String ERROR_MESSAGE_NOT_NULL_DOWN_STATION = "하행선은 필수입니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;
    @Embedded
    private Distance distance;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Distance distance) {
        validEmptyStation(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validEmptyStation(Station upStation, Station downStation) {
        if (Objects.isNull(upStation)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_NULL_UP_STATION);
        }

        if (Objects.isNull(downStation)) {
            throw new IllegalArgumentException(ERROR_MESSAGE_NOT_NULL_DOWN_STATION);
        }
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public void toLine(Line line) {
        this.line = line;
    }

    public void updateUpStation(Section newSection) {
        this.distance = this.distance.subtract(newSection.distance);
        this.upStation = newSection.downStation;
    }

    public void updateDownStation(Section newSection) {
        this.distance = this.distance.subtract(newSection.distance);
        this.downStation = newSection.upStation;
    }

    public boolean isSameUpStation(Section section) {
        return upStation.isSameStation(section.getUpStation());
    }

    public boolean isSameDownStation(Section section) {
        return downStation.isSameStation(section.getDownStation());
    }

    public List<Station> stations() {
        List<Station> stations = new ArrayList<>();
        stations.add(upStation);
        stations.add(downStation);
        return stations;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance.getDistance();
    }
}
