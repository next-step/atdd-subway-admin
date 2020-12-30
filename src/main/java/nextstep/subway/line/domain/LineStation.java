package nextstep.subway.line.domain;

import com.sun.istack.NotNull;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

/**
 * @author : leesangbae
 * @project : jpa
 * @since : 2020-12-17
 */
@Entity
public class LineStation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pre_station_id")
    private Station preStation;

    private long distance;

    protected LineStation() {
    }

    public LineStation(Line line, Station station, Station preStation, long distance) {
        validate(line, station, preStation);
        this.line = line;
        this.station = station;
        this.preStation = preStation;
        this.distance = distance;
        this.line.add(this);
        this.station.add(this);
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getStation() {
        return station;
    }

    public long getDistance() {
        return distance;
    }

    private void validate(Line line, Station station, Station preStation) {
        if (line == null || station == null) {
            throw new IllegalArgumentException("LineStation line, station는 필수 값 입니다.");
        }

        if (station.equals(preStation)) {
            throw new IllegalArgumentException("이전역과 지금역은 같을 수 없습니다.");
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
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
