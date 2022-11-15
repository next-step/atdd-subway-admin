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
        if (isEqualUpStation(newSection)) {
            updateUpStation(newSection);
        }

        if (isEqualDownStation(newSection)) {
            updateDownStation(newSection);
        }
    }

    private boolean isEqualUpStation(Section newSection) {
        return upStation.equals(newSection.upStation);
    }

    private boolean isEqualDownStation(Section newSection) {
        return downStation.equals(newSection.downStation);
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
