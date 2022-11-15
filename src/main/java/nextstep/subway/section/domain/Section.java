package nextstep.subway.section.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.domain.BaseEntity;
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
    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        if(distance <= 0) {
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

    public int getDistance() {
        return distance;
    }

    public void update(Section newSection) {
        if(distance <= newSection.distance){
            throw new IllegalArgumentException("추가하는 노선의 길이는 기존 역 사이의 길이보다 크거나 같을 수 없습니다.");
        }
        if (isEqualUpStation(newSection.upStation)) {
            updateUpStation(newSection);
        }
        if (isEqualDownStation(newSection.downStation)) {
            updateDownStation(newSection);
        }
    }

    public boolean isEqualUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualDownStation(Station station) {
        return downStation.equals(station);
    }

    public void updateUpStation(Section newSection) {
        upStation = newSection.upStation;
        distance = distance - newSection.distance;
    }

    private void updateDownStation(Section newSection) {
        downStation = newSection.upStation;
        distance = distance - newSection.distance;
    }

    public Section merge(Section nextSection) {
        int distance = this.distance + nextSection.getDistance();
        Section section = new Section(upStation, nextSection.downStation, distance);
        section.addLine(line);
        return section;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
