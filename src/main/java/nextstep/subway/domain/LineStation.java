package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class LineStation extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;
    private int distance;

    protected LineStation() {
    }

    public LineStation(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        if(distance <= 0){
            throw new IllegalArgumentException("노선의 길이는 0보다 커야합니다.");
        }
        this.distance = distance;
    }

    public List<Station> findStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public Long getId() {
        return id;
    }

    public void update(LineStation newLineStation) {
        if(distance <= newLineStation.distance){
            throw new IllegalArgumentException("추가하는 노선의 길이는 기존 역 사이의 길이보다 크거나 같을 수 없습니다.");
        }
        if (isEqualUpStation(newLineStation)) {
            updateUpStation(newLineStation);
        }
        if (isEqualDownStation(newLineStation)) {
            updateDownStation(newLineStation);
        }
    }

    private boolean isEqualUpStation(LineStation newLineStation) {
        return upStation.equals(newLineStation.upStation);
    }

    private void updateUpStation(LineStation newLineStation) {
        upStation = newLineStation.upStation;
        distance = distance - newLineStation.distance; // TODO: 중복코드
    }

    private boolean isEqualDownStation(LineStation newLineStation) {
        return downStation.equals(newLineStation.downStation);
    }

    private void updateDownStation(LineStation newLineStation) {
        downStation = newLineStation.upStation;
        distance = distance - newLineStation.distance;
    }
}
