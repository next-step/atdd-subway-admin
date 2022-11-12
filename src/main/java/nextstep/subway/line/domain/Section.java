package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;
import nextstep.subway.utils.NumberUtil;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Line line;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station upStation;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validate(line, upStation, downStation, distance);

        updateLine(line);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    private void validate(Line line, Station upStation, Station downStation, int distance) {
        validateLine(line);
        validateStations(upStation, downStation);
        validateDistance(distance);
    }

    private void validateLine(Line line) {
        if(Objects.isNull(line)) {
            throw new IllegalArgumentException(SectionExceptionCode.REQUIRED_LINE.getMessage());
        }
    }

    private void validateStations(Station upStation, Station downStation) {
        if(Objects.isNull(upStation)) {
            throw new IllegalArgumentException(SectionExceptionCode.REQUIRED_UP_STATION.getMessage());
        }

        if(Objects.isNull(downStation)) {
            throw new IllegalArgumentException(SectionExceptionCode.REQUIRED_DOWN_STATION.getMessage());
        }

        if(upStation.equals(downStation)) {
            throw new IllegalArgumentException(
                    SectionExceptionCode.CANNOT_BE_THE_SAME_EACH_STATION.getMessage());
        }
    }

    private void validateDistance(int distance) {
        if(!NumberUtil.isPositiveNumber(distance)) {
            throw new IllegalArgumentException(SectionExceptionCode.INVALID_DISTANCE.getMessage());
        }
    }

    void updateLine(Line line) {
        if(this.line != line) {
            this.line = line;
            line.addSection(this);
        }
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Section section = (Section) o;
        return Objects.equals(line, section.line)
                    && Objects.equals(upStation, section.upStation)
                        && Objects.equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation);
    }
}
