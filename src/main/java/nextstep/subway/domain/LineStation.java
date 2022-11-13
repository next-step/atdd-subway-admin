package nextstep.subway.domain;

import com.google.common.collect.Lists;
import nextstep.subway.exception.CannotAddSectionException;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static nextstep.subway.exception.CannotAddSectionException.NO_MATCHED_STATION;

@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private Integer distance;

    protected LineStation() {
    }

    public LineStation(Line line, Station upStation, Station downStation, Integer distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public boolean canAddSection(Station otherUpStation, Station otherDownStation) {
        verifyNotUpStationDownStation(otherUpStation, otherDownStation);
        return canSplitByUpStation(otherUpStation, otherDownStation)
                || canSplitByDownStation(otherUpStation, otherDownStation)
                || isUpStation(otherDownStation)
                || isDownStation(otherUpStation);
    }

    private void verifyNotUpStationDownStation(Station otherUpStation, Station otherDownStation) {
        if (isUpStation(otherUpStation) && isDownStation(otherDownStation)) {
            throw new CannotAddSectionException(CannotAddSectionException.UP_AND_DOWN_STATION_ALL_EXISTS);
        }
    }

    private boolean isUpStation(Station otherUpStation) {
        return this.upStation.equals(otherUpStation);
    }

    private boolean isDownStation(Station otherDownStation) {
        return downStation.equals(otherDownStation);
    }

    private boolean canSplitByDownStation(Station otherUpStation, Station otherDownStation) {
        return !isUpStation(otherUpStation) && isDownStation(otherDownStation);
    }

    private boolean canSplitByUpStation(Station otherUpStation, Station otherDownStation) {
        return isUpStation(otherUpStation) && !isDownStation(otherDownStation);
    }

    public List<LineStation> addSection(Station upStation, Station downStation, Integer distance) {
        if (canSplitByUpStation(upStation, downStation)) {
            return splitSectionByUpStation(downStation, distance);
        }
        if (canSplitByDownStation(upStation, downStation)) {
            return splitSectionByDownStation(upStation, distance);
        }
        if (canAddOnUpStation(downStation)) {
            return addOnUpStation(upStation, distance);
        }
        if (canAddOnDownStation(upStation)) {
            return addOnDownStation(downStation, distance);
        }
        throw new CannotAddSectionException(NO_MATCHED_STATION);
    }

    private List<LineStation> addOnUpStation(Station appendedUpStation, Integer distance) {
        return Lists.newArrayList(
            new LineStation(line, appendedUpStation, this.upStation, distance),
            this
        );
    }

    private List<LineStation> addOnDownStation(Station appendedDownStation, Integer distance) {
        return Lists.newArrayList(
                this,
            new LineStation(line, this.downStation, appendedDownStation, distance)
        );
    }

    private boolean canAddOnUpStation(Station downStation) {
        return upStation.equals(downStation);
    }

    private boolean canAddOnDownStation(Station upStation) {
        return downStation.equals(upStation);
    }

    public List<LineStation> splitSectionByUpStation(Station appendedStation, Integer distance) {
        verifyDistance(distance);
        return addSection(appendedStation, distance,this.distance - distance);
    }

    public List<LineStation> splitSectionByDownStation(Station appendedStation, Integer distance) {
        verifyDistance(distance);
        return addSection(appendedStation, this.distance - distance, distance);
    }

    private void verifyDistance(Integer distance) {
        if (this.distance <= distance) {
            throw new CannotAddSectionException(CannotAddSectionException.LONGER_THAN_SECTION);
        }
    }

    private List<LineStation> addSection(Station appendedStation, Integer upDistance, Integer downDistance) {
        return Lists.newArrayList(
                new LineStation(line, this.upStation, appendedStation, upDistance),
                new LineStation(line, appendedStation, this.downStation, downDistance)
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStation that = (LineStation) o;
        return line.equals(that.line)
                && upStation.equals(that.upStation)
                && downStation.equals(that.downStation)
                && distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(line, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "LineStation{" +
                "line=" + line.getName() +
                ", upStation=" + upStation.getName() +
                ", downStation=" + downStation.getName() +
                ", distance=" + distance +
                '}';
    }
}
