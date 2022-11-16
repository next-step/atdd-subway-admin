package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "SECTION")
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;
    private Integer distance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    private Section(Long id, Station upStation, Station downStation, Integer distance, Line line) {
        validateStation(upStation);
        validateStation(downStation);
        validateDistance(distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
        this.line = line;
    }

    public static Section of(Station upStation, Station downStation, Integer distance) {
        return new Section(null, upStation, downStation, distance, null);
    }

    public static Section of(Station upStation, Station downStation, Integer distance, Line line) {
        return new Section(null, upStation, downStation, distance, line);
    }

    private void validateStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("구간은 상행역, 하행역 둘다 존재해야 합니다.");
        }
    }

    private void validateDistance(Integer distance) {
        if (distance == null || distance < 1) {
            throw new IllegalArgumentException("구간길이는 1 이상이여 합니다.");
        }
    }

    public List<Station> getStations() {
        return Arrays.asList(upStation, downStation);
    }

}
