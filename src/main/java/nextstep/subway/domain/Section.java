package nextstep.subway.domain;

import nextstep.subway.exception.InvalidDistanceException;
import nextstep.subway.exception.InvalidSectionException;

import javax.persistence.*;

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
        validateIncludeAnyStation(line, upStation, downStation);
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
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

    private void validateIncludeAnyStation(Line line, Station newUpStation, Station newDownStation) {
        if (line.isSameAnyStation(newUpStation, newDownStation)) {
            return;
        }

        throw new InvalidSectionException(line.getUpStation().getId(), line.getDownStation().getId());
    }

    public boolean isExistsSection(Section newSection) {
        if (!upStation.equals(newSection.getUpStation())) {
            return false;
        }

        if (!downStation.equals(newSection.getDownStation())) {
            return false;
        }

        return true;
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
