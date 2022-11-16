package nextstep.subway.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "line_station")
public class LineStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    private int distance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "upStationId")
    private Station upStation;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "downStationId")
    private Station downStation;

    protected LineStation() {
    }

    public LineStation(Line line, int distance, Station upStation, Station downStation) {
        this.line = line;
        this.distance = distance;
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Long getId() {
        return id;
    }

    public Long getLineId() {
        return line.getId();
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    public boolean isEqualsUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isEqualsDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LineStation)) {
            return false;
        }
        LineStation that = (LineStation) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public LineStation createNewLineStation(int distance, Station upStation, Station downStation) {
        return new LineStation(line, distance, upStation, downStation);
    }

    public LineStation createNewDownLineStation(int distance, Station downStation) {
        return new LineStation(line, Math.abs(this.distance - distance), this.downStation, downStation);
    }

    public LineStation createNewUpLineStation(int distance, Station upStation) {
        return new LineStation(line, Math.abs(this.distance - distance), this.upStation, upStation);
    }

    public void validateLength(int distance) {
        if (this.distance <= distance) {
            throw new IllegalArgumentException("기존역 사이에 같은길이의 역을 등록할 수 없습니다.");
        }
    }
}
