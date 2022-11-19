package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class LineStation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    private int distance;


    protected LineStation() {
    }

    private LineStation(Station upStation, Station downStation, int distance) {
        this.id = null;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static LineStation of(Station upStation, Station downStation, int distance) {
        return new LineStation(upStation, downStation, distance);
    }

    public List<Station> getRelationStation() {
        return Arrays.asList(upStation, downStation);
    }

    private void checkAlreadyExistStation(Station upStation, Station downStation) {
        if (isSameUpStation(upStation) && isSameDownStation(downStation)) {
            throw new IllegalArgumentException("이미 존재해요");
        }
    }

    private boolean isSameUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    private boolean isSameDownStation(Station downStation) {
        return this.downStation.equals(downStation);
    }

    public void update(LineStation newStation) {
        if (isSameUpStation(newStation.upStation)) {
            updateUpStation(newStation);
        }

        if (isSameDownStation(newStation.downStation)) {
            updateDownStation(newStation);
        }
    }

    private void updateDownStation(LineStation newStation) {
        this.downStation = newStation.downStation;
        this.distance = distance - newStation.distance;
    }

    private void updateUpStation(LineStation newStation) {
        this.upStation = newStation.upStation;
        this.distance = distance - newStation.distance;
    }
}
