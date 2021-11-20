package nextstep.subway.line.domain.LineStation;

import nextstep.subway.excetpion.ErrorCode;
import nextstep.subway.line.domain.Line.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.StationNotFoundException;

import javax.persistence.*;

import static nextstep.subway.common.utils.ValidationUtils.isNull;

@Entity
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_line_station_preStation"))
    private Station preStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "next_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_line_station_nextStation"))
    private Station nextStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_line"))
    private Line line;

    @Embedded
    private Distance distance;

    protected LineStation() {
    }

    public LineStation(Line line, Station preStation, Station nextStation, int distance) {
        lineStationValidation(line, nextStation, preStation, distance);
        this.line = line;
        this.nextStation = nextStation;
        this.preStation = preStation;
        this.distance = new Distance(distance);
    }

    private void lineStationValidation(Line line, Station preStation, Station nextStation, int distance) {
        if (isNull(line)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "구간 정보가 없습니다.");
        }
        if (isNull(preStation)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "상행 정보가 없습니다.");
        }
        if (isNull(nextStation)) {
            throw new StationNotFoundException(ErrorCode.NOT_FOUND_ENTITY, "하행 정보가 없습니다.");
        }
    }

    public boolean isSame(LineStation lineStation) {
        return preStation.equals(lineStation.preStation) && nextStation.equals(lineStation.nextStation);
    }

    public void updateToPreStation(Station station) {
        this.preStation = station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Station getPreStation() {
        return preStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public void updateToNextStation(Station station) {
        this.nextStation = station;
    }

    public void plusDistance(Distance distance) {
        this.distance.plus(distance);
    }

    public void minusDistance(Distance distance) {
        this.distance.minus(distance);
    }
}
