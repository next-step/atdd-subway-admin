package nextstep.subway.line.domain;

import lombok.Builder;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Entity
@Table(indexes = {
        @Index(unique = true, columnList = "up_station_id,down_station_id")
})
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    protected Section() {
    }

    @Builder
    private Section(Line line, Station upStation, Station downStation, int distance) {
        validate(line, upStation, downStation, distance);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateUpStation(Station upStation) {
        this.upStation = upStation;
    }

    public void updateDownStation(Station downStation) {
        this.downStation = downStation;
    }

    public void update(int distance) {
        validateRanged(distance);
        this.distance = distance;
    }

    private void validate(Line line, Station upStation, Station downStation, int distance) {
        validateRequired(line, upStation, downStation);
        validateRanged(distance);
    }

    private void validateRequired(Line line, Station upStation, Station downStation) {
        if (line == null || upStation == null || downStation == null) {
            throw new IllegalArgumentException("필수값 누락입니다.");
        }
    }

    private void validateRanged(int distance) {
        if (distance <= 0) {
            throw new IllegalArgumentException("거리는 0보다 커야합니다.");
        }
    }

    public boolean isSameDownStation(Section section) {
        return this.downStation == section.downStation;
    }

    public boolean isSameUpStation(Section section) {
        return this.upStation == section.upStation;
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

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void replaceDownStation(Section section) {
        validateReplace(section);
        updateDownStation(section.getUpStation());
        update(getReplaceDistance(section.getDistance()));
    }

    private int getReplaceDistance(int distance) {
        return this.getDistance() - distance;
    }

    private void validateReplace(Section section) {
        Objects.requireNonNull(section);
        if (this.getDistance() <= section.getDistance()) {
            throw new IllegalArgumentException(String.format("거리가 %dm보다 짧아야합니다.", this.getDistance()));
        }
    }

    public void replaceUpStation(Section section) {
        validateReplace(section);
        updateUpStation(section.getDownStation());
    }
}
