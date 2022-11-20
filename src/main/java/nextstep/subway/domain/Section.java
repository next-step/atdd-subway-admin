package nextstep.subway.domain;

import static nextstep.subway.constant.Constant.ADD_SECTION_FAIL_CAUSE_DISTANCE;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {
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

    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateUpStation(Station newUpStation) {
        this.upStation = newUpStation;
    }

    public void updateDownStation(Station newDownStation) {
        this.downStation = newDownStation;
    }

    public void plusDistance(int distance) {
        this.distance += distance;
    }

    public void minusDistance(int distance) {
        validateDistance(distance);
        this.distance -= distance;
    }

    public boolean isEqualToUpStation(Station station) {
        return this.upStation == station;
    }

    public boolean isEqualToDownStation(Station station) {
        return this.downStation == station;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    private void validateDistance(int distance){
        if(this.distance <= distance){
            throw new IllegalArgumentException(ADD_SECTION_FAIL_CAUSE_DISTANCE);
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
        Section that = (Section) o;
        return distance == that.distance && Objects.equals(id, that.id) && Objects.equals(line,
                that.line) && Objects.equals(upStation, that.upStation) && Objects.equals(downStation,
                that.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
