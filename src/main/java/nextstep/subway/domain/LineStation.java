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
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;


    protected LineStation() {
    }

    private LineStation(Station upStation, Station downStation, int distance) {
        this.id = null;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public static LineStation of(Station upStation, Station downStation, int distance) {
        return new LineStation(upStation, downStation, distance);
    }

    public List<Station> getRelationStation() {
        return Arrays.asList(upStation, downStation);
    }

    public boolean isSameUpStation(Station upStation) {
        return this.upStation.equals(upStation);
    }

    public boolean isSameDownStation(Station downStation) {
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

    public void updateForDelete(LineStation downStation) {
        this.upStation = downStation.upStation;
    }

    private void updateDownStation(LineStation newStation) {
        this.downStation = newStation.upStation;
        this.distance = distance.minus(newStation.distance);
    }

    private void updateUpStation(LineStation newStation) {
        this.upStation = newStation.downStation;
        this.distance = distance.minus(newStation.distance);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public LineStation renewal(LineStation downStation) {
        Distance renewDistance = this.distance.plus(downStation.distance);
        LineStation renewLineStation
                = LineStation.of(upStation, downStation.downStation, renewDistance.getDistance());
        renewLineStation.addLine(line);
        return renewLineStation;
    }
}
