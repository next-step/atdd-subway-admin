package nextstep.subway.domain;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import javax.persistence.*;

@Entity
public class Section {
    private static final String ERROR_MSG_UP_STATION_EMPTY = "상행종점역 정보가 존재하지 않습니다.";
    private static final String ERROR_MSG_DOWN_STATION_EMPTY = "하행종점역 정보가 존재하지 않습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;
    @Column
    private Distance distance;

    public static class Builder {
        private final Station upStation;
        private final Station downStation;
        private final int distance;

        public Builder(Station upStation, Station downStation, int distance) {
            this.upStation = upStation;
            this.downStation = downStation;
            this.distance = distance;
        }

        public Section build() {
            return new Section(this);
        }
    }

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        validate(upStation, downStation);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Builder builder) {
        validate(builder.upStation, builder.downStation);
        this.upStation = builder.upStation;
        this.downStation = builder.downStation;
        this.distance = new Distance(builder.distance);
    }

    private void validate(Station upStation, Station downStation) {
        validateUpStation(upStation);
        validateDownStation(downStation);
    }

    private void validateUpStation(Station upStation) {
        if (ObjectUtils.isEmpty(upStation)) {
            throw new IllegalArgumentException(ERROR_MSG_UP_STATION_EMPTY);
        }
    }

    private void validateDownStation(Station downStation) {
        if (ObjectUtils.isEmpty(downStation)) {
            throw new IllegalArgumentException(ERROR_MSG_DOWN_STATION_EMPTY);
        }
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

    public Distance getDistance() {
        return distance;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void update(Section newSection) {
        if (this.upStation.equals(newSection.upStation)) {
            updateUpStation(newSection);
        }
        if (this.downStation.equals(newSection.downStation)) {
            updateDownStation(newSection);
        }
    }

    private void updateUpStation(Section section) {
        this.upStation = section.downStation;
        updateDistance(section.distance);
    }

    private void updateDownStation(Section section) {
        this.downStation = section.upStation;
        updateDistance(section.distance);
    }

    private void updateDistance(Distance newDistance) {
        this.distance.setDistanceGap(newDistance);
    }

    public void merge(Section other) {
        this.downStation = other.getDownStation();
        this.distance.add(other.distance);
    }
}
