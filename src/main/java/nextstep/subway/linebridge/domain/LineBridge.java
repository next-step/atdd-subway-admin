package nextstep.subway.linebridge.domain;

import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.distance.Distance;
import nextstep.subway.station.domain.Station;
import nextstep.subway.line.domain.Line;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class LineBridge extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_lineBridge_to_up_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_lineBridge_to_down_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_lineBridge_to_line"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @Embedded
    private Distance distance;


    public LineBridge(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public LineBridge(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public LineBridge() {

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

    public int getDistance() {
        return distance.getValue();
    }

    public Line getLine() {
        return line;
    }

    public void updateLine(Line line) {
        this.line = line;
    }

    public void removeLine() {
        this.line = null;
    }


    public void repair(LineBridge lineBridge) {
        if (Objects.isNull(lineBridge)) {
            return;
        }
        repairStation(lineBridge);
    }

    private void repairStation(LineBridge lineBridge) {
        if (isSameUpStation(lineBridge.upStation)) {
            repairUpStation(lineBridge);
            return;
        }

        if (isSameDownStation(lineBridge.downStation)) {
            repairDownStation(lineBridge);
        }
    }

    private void repairUpStation(LineBridge lineBridge) {
        this.upStation = lineBridge.downStation;
        changeDistance(lineBridge.distance);
    }

    private void repairDownStation(LineBridge lineBridge) {
        this.downStation = lineBridge.upStation;
        changeDistance(lineBridge.distance);
    }

    private void changeDistance(Distance distance) {
        this.distance.subtract(distance.getValue());
    }

    public boolean isSame(LineBridge lineBridge) {
        if (Objects.isNull(lineBridge)) {
            return false;
        }
        return isSameUpStation(lineBridge.upStation) && isSameDownStation(lineBridge.downStation);
    }

    public boolean isAnyMatch(LineBridge lineBridge) {
        if (Objects.isNull(lineBridge)) {
            return false;
        }
        return isSameUpStation(lineBridge.upStation) || isSameDownStation(lineBridge.downStation)
                || isSameUpStation(lineBridge.downStation) || isSameDownStation(lineBridge.upStation);
    }

    private boolean isSameUpStation(Station station) {
        return this.upStation.equals(station);
    }

    private boolean isSameDownStation(Station station) {
        return this.downStation.equals(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineBridge lineBridge = (LineBridge) o;
        return Objects.equals(id, lineBridge.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
