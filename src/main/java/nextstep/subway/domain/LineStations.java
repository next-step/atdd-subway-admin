package nextstep.subway.domain;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.Objects;

@Embeddable
public class LineStations {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "UP_STATION_ID")
    private Station upStation;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "DOWN_STATION_ID")
    private Station downStation;

    protected LineStations() {
    }

    public LineStations(Station upStation, Station downStation) {
        this.upStation = upStation;
        this.downStation = downStation;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LineStations)) return false;
        LineStations that = (LineStations) o;
        return Objects.equals(getUpStation(), that.getUpStation()) && Objects.equals(getDownStation(), that.getDownStation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUpStation(), getDownStation());
    }

    @Override
    public String toString() {
        return "LineStations{" +
                '}';
    }
}
