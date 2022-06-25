package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.InvalidSectionException;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Long distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, Long distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, Long distance) {
        validateIncludeAnyStation(line, upStation, downStation);
        return new Section(line, upStation, downStation, distance);
    }

    private static void validateIncludeAnyStation(Line line, Station newUpStation, Station newDownStation) {
        if (line.includeAnyStation(newUpStation, newDownStation)) {
            return;
        }

        throw new InvalidSectionException();
    }

    public void validateDistance() {
        if (!isBetweenSection(line, upStation, downStation)) {
            return;
        }

        if (distance < line.getDistance()) {
            return;
        }

        throw new InvalidDistanceException(line.getDistance());
    }

    private boolean isBetweenSection(Line line, Station newUpStation, Station newDownStation) {
        if (line.isSameUpStation(newUpStation)) {
            return true;
        }

        if (line.isSameDownStation(newDownStation)) {
            return true;
        }

        return false;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Long getDistance() {
        return distance;
    }
}
