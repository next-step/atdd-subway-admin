package nextstep.subway.domain.line;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.constants.ErrorMessage;
import nextstep.subway.domain.BaseEntity;
import nextstep.subway.domain.station.Station;
import nextstep.subway.domain.station.StationPosition;
import nextstep.subway.domain.station.StationRegisterStatus;
import nextstep.subway.domain.station.StationStatus;

@Entity
public class LineStation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_line_station_station"))
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_station_id", foreignKey = @ForeignKey(name = "fk_line_station_pre_station"))
    private Station preStation;

    @Embedded
    private Distance distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_line_station_to_line"))
    private Line line;

    public LineStation() {
    }

    public LineStation(Station station, Station preStation, Distance distance, Line line) {
        this.station = station;
        this.preStation = preStation;
        this.distance = distance;
        this.line = line;
    }

    public StationStatus checkStationStatus(Station station) {
        if (station.equals(this.station)) {
            return new StationStatus(station, this, StationPosition.DOWNSTATION);
        }
        if (station.equals(this.preStation)) {
            return new StationStatus(station, this, StationPosition.UPSTATION);
        }
        return null;
    }

    public int distanceCompare(Distance distance) {
        return this.distance.compareTo(distance);
    }

    public void splitLineStation(StationPosition stationPosition, Station interStation, Distance distanceToSubtract) {
        if (stationPosition == StationPosition.UPSTATION) {
            preStation = interStation;
        }
        if (stationPosition == StationPosition.DOWNSTATION) {
            station = interStation;
        }
        distance.subtract(distanceToSubtract);
    }

    public void validate(LineStations lineStations) {
        StationRegisterStatus upStationStatus = lineStations.getStationRegisterStatus(preStation);
        StationRegisterStatus downStationStatus = lineStations.getStationRegisterStatus(station);
        checkRegisteredStationExist(upStationStatus, downStationStatus);
        checkBothStationRegistered(upStationStatus, downStationStatus);
        upStationStatus.validate(StationPosition.UPSTATION, distance, station);
        downStationStatus.validate(StationPosition.DOWNSTATION, distance, preStation);
    }

    private void checkRegisteredStationExist(StationRegisterStatus upStationStatus,
            StationRegisterStatus downStationStatus) {
        if (isStationsEmpty(upStationStatus, downStationStatus)) {
            throw new IllegalArgumentException(ErrorMessage.BOTH_STATIONS_NOT_REGISTERED);
        }
    }

    private void checkBothStationRegistered(StationRegisterStatus upStationStatus,
            StationRegisterStatus downStationStatus) {
        if (isStationsExist(upStationStatus, downStationStatus)) {
            throw new IllegalArgumentException(ErrorMessage.ALREADY_REGISTERED_SECTION);
        }
    }

    private boolean isStationsEmpty(StationRegisterStatus upStationStatus, StationRegisterStatus downStationStatus) {
        return upStationStatus.isEmpty() && downStationStatus.isEmpty();
    }

    private boolean isStationsExist(StationRegisterStatus upStationStatus, StationRegisterStatus downStationStatus) {
        return !upStationStatus.isEmpty() && !downStationStatus.isEmpty();
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Long getStationId() {
        return station.getId();
    }

    public String getStationName() {
        return station.getName();
    }

    public Station getPreStation() {
        return preStation;
    }

    public Long getPreStationId() {
        return preStation.getId();
    }

    public String getPreStationName() {
        return preStation.getName();
    }

    public int getDistance() {
        return distance.getDistance();
    }

    public Line getLine() {
        return line;
    }

    public Long getLineId() {
        return line.getId();
    }

    public String getLineName() {
        return line.getName();
    }

    public void addLine(Line line) {
        this.line = line;
        if (!line.containLineStation(this)) {
            line.addLineStationWithoutSettingLine(this);
        }
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
        return Objects.equals(id, that.id) && Objects.equals(station, that.station)
                && Objects.equals(preStation, that.preStation) && Objects.equals(distance,
                that.distance) && Objects.equals(line, that.line);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, station, preStation, distance, line);
    }
}
