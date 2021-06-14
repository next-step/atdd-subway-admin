package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section(){}

    public Section(Line line, Station upStation, Station downStation, int distance) {
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

    public int getDistance() {
        return distance;
    }

    public void update(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public void updateUpStation(Section newSection) {
        checkDistance(newSection.getDistance());
        this.upStation = newSection.getDownStation();
        this.distance -= newSection.getDistance();
    }

    public void updateDownStation(Section newSection) {
        checkDistance(newSection.getDistance());
        this.downStation = newSection.getUpStation();
        this.distance -= newSection.getDistance();
    }

    private void checkDistance(int distance){
        if (this.distance < distance) {
            throw new IllegalArgumentException("기존 구간보다 긴 거리값은 추가할수 없습니다.");
        }
    }

    public boolean isEqualsUpStation(Station inputUpStation) {
        return upStation.equals(inputUpStation);
    }

    public boolean isEqualsDownStation(Station inputDownStation) {
        return downStation.equals(inputDownStation);
    }
}
