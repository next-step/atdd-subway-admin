package nextstep.subway.line.domain;

import java.security.InvalidParameterException;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.station.domain.Station;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

@Entity
@Where(clause = "deleted = false")
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"), nullable = false)
    private Line line;

    @ManyToOne
    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_section_to_station"), nullable = false)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "next_station_id", foreignKey = @ForeignKey(name = "fk_section_to_next_station"))
    private Station nextStation;

    @Column(name = "distance")
    @ColumnDefault("0")
    private Integer distance = 0;

    @Column(name = "deleted")
    private boolean deleted = Boolean.FALSE;

    protected Section() {
    }

    public Section(Line line, Station station, Station nextStation, Integer distance) {
        validateNotNull(line, station, distance);
        this.line = line;
        this.station = station;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public static Section lastOf(Section section) {
        return new Section(section.getLine(), section.getNextStation(), null,
            section.getDistance());
    }

    public void delete() {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void nextStationUpdate(Station station) {
        this.nextStation = station;
    }

    public void stationUpdate(Station station) {
        this.station = station;
    }

    public Station getStation() {
        return station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Line getLine() {
        return line;
    }

    public Integer getDistance() {
        return distance;
    }

    private void validateNotNull(Line line, Station station, Integer distance) {
        if (Objects.isNull(line)) {
            throw new InvalidParameterException("노선은 필수 정보 입니다.");
        }

        if (Objects.isNull(station)) {
            throw new InvalidParameterException("상행 지하철역은 필수 정보 입니다.");
        }

        if (Objects.isNull(distance) || distance == 0) {
            throw new InvalidParameterException("구간 거리는 필수 정보 입니다.");
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

        if (Objects.isNull(id)) {
            return false;
        }

        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
