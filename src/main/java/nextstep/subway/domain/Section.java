package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;
    @Embedded
    private Distance distance;

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    protected Section() {}

    public List<Station> findStations() {
        return Arrays.asList(upStation, downStation);
    }

    public void addLine(Line line) {
        this.line = line;
    }

    public void update(Section newSection) {
        if (upStation.equals(newSection.upStation)) {
            updateUpStation(newSection);
        }

        if (downStation.equals(newSection.downStation)) {
            updateDownStation(newSection);
        }
    }

    private void updateUpStation(Section newSection) {
        upStation = newSection.downStation;
        distance = distance.subtract(newSection.distance);
    }

    private void updateDownStation(Section newSection) {
        downStation = newSection.upStation;
        distance = distance.subtract(newSection.distance);
    }
}
