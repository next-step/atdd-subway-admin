package nextstep.subway.line.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.SectionExceptionCode;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
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

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateStations(upStation, downStation);

        updateLine(line);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
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

    void updateLine(Line line) {
        if(this.line != line) {
            this.line = line;
            line.addSection(this);
        }
    }

    public void divideSection(Section request) {
        this.distance = distance.minus(request.getDistance());
        this.upStation = request.downStation;
    }

    public void extendSection(Section request) {
        this.distance = distance.plus(request.getDistance());
        this.downStation = request.downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

    public int getDistance() {
        return distance.getDistance();
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
