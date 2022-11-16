package nextstep.subway.section.domain;

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
import nextstep.subway.common.domain.BaseEntity;
import nextstep.subway.common.exception.ErrorMessageConstant;
import nextstep.subway.station.domain.Station;
import nextstep.subway.line.domain.Line;

@Entity
public class Section extends BaseEntity {
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
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
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

    public Station getDownStation() {
        return downStation;
    }

    public void update(Section newSection) {
        distance.validNewDistance(newSection.distance.get());
        if (isEqualUpStation(newSection.upStation)) {
            updateUpStation(newSection);
        }
        if (isEqualDownStation(newSection.downStation)) {
            updateDownStation(newSection);
        }
    }

    public Section merge(Section nextSection) {
        Distance newDistance = distance.add(nextSection.distance);
        Section section = new Section(upStation, nextSection.getDownStation(), newDistance);
        section.addLine(line);
        return section;
    }

    public boolean isEqualUpStation(Station station) {
        return upStation.equals(station);
    }

    private void updateUpStation(Section newSection) {
        upStation = newSection.upStation;
        distance.subtract(newSection.distance);
    }

    public boolean isEqualDownStation(Station station) {
        return downStation.equals(station);
    }

    private void updateDownStation(Section newSection) {
        downStation = newSection.upStation;
        distance.subtract(newSection.distance);
    }
}
