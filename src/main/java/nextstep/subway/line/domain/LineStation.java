package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.station.domain.Station;

@Entity
public class LineStation extends BaseEntity implements Comparable<LineStation> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @Enumerated(EnumType.STRING)
    private LineStationType lineStationType;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "next_station_id")
    private Station nextStation;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected LineStation() {
    }

    private LineStation(Distance distance, LineStationType lineStationType, Station station) {
        this.distance = distance;
        this.lineStationType = lineStationType;
        setStation(station);
    }

    public static LineStation fromDownSection(Station station, Line line) {
        return new LineStation(Distance.createDownDistance(), LineStationType.DOWN, station).toLine(line);
    }

    public static LineStation ofUpSection(Station station, Line line, Distance distance, Station nextStation) {
        return new LineStation(distance, LineStationType.UP, station).toLine(line).withNextStation(nextStation);
    }

    public static LineStation ofMiddleSection(Station station, Line line, Distance distance, Station nextStation) {
        return new LineStation(distance, LineStationType.MIDDLE, station).toLine(line).withNextStation(nextStation);
    }

    /**
     * 연관관계 편의 메서드
     *
     * @param line
     */
    public LineStation toLine(Line line) {
        if (this.line != null) {
            this.line.removeSection(this);
        }

        this.line = line;
        if (!line.containsSection(this)) {
            line.addSection(this);
        }
        return this;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        if (station == null) {
            throw new BusinessException(Messages.SECTION_REQUIRED_STATION.getValues());
        }
        this.station = station;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public LineStation withNextStation(Station station) {
        if (station == null) {
            return this;
        }
        this.nextStation = station;

        return this;
    }

    public boolean isDownStation() {
        if (this.lineStationType.equals(LineStationType.DOWN)) {
            return true;
        }
        return false;
    }

    public boolean isUpStation() {
        if (this.lineStationType.equals(LineStationType.UP)) {
            return true;
        }
        return false;
    }

    public Distance calculateDistance(Distance distance) {
        try {
            return this.distance.minus(distance);
        }catch (BusinessException e) {
            throw new BusinessException(Messages.LONG_OR_SAME_DISTANCE.getValues());
        }
    }

    public boolean hasNextStation(Station nextStation) {
        if (this.nextStation.equals(nextStation)) {
            return true;
        }
        return false;
    }

    public boolean equalsLine(Line line) {
        return this.line.equals(line);
    }

    public boolean hasStation(Station station) {
        return this.station.equals(station);
    }

    public LineStation update(LineStationType lineStationType) {
        return update(this.distance, lineStationType, this.nextStation);
    }

    public LineStation update(Distance distance, Station linkStation) {
        return update(distance, this.lineStationType, linkStation);
    }

    public LineStation update(Distance distance, LineStationType lineStationType, Station linkStation) {
        this.distance = distance;
        this.lineStationType = lineStationType;
        this.nextStation = linkStation;
        return this;
    }

    @Override
    public int compareTo(LineStation o) {

        if (lineStationType.equals(LineStationType.UP)) {
            return -1;
        }

        if (nextStation == null) {
            return 1;
        }

        if (o.nextStation == null) {
            return -1;
        }

        if (nextStation.equals(o.station)) {
            return -1;
        }

        if (station.equals(o.nextStation)) {
            return 1;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineStation lineStation = (LineStation) o;
        return Objects.equals(id, lineStation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", distance=" + distance +
            ", sectionType=" + lineStationType +
            ", station=" + station +
            ", linkStation=" + nextStation +
            ", line=" + line.getName() +
            '}';
    }


}
