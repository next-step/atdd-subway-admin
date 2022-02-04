package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.line.exception.SectionAlreadyExistInTheLineException;
import nextstep.subway.line.exception.SectionDistanceExceededException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {

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

    private int distance;

    public Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(line, upStation, downStation, distance);
    }

    public boolean isUp(Section section) {
        return this.downStation.equals(section.getUpStation());
    }

    public boolean isDown(Section section) {
        return this.upStation.equals(section.getDownStation());
    }

    public void addInnerSection(Section section) {
        if (hasEqualUpStation(section)) {
            addUpwardInnerSection(section);
        }
        if (hasEqualDownStation(section)) {
            addDownWardInnerSection(section);
        }
    }

    private void addUpwardInnerSection(Section section) {
        checkInnerDistance(section.getDistance());
        this.distance -= section.getDistance();
        this.upStation = section.getDownStation();
    }

    private void addDownWardInnerSection(Section section) {
        checkInnerDistance(section.getDistance());
        this.distance -= section.getDistance();
        this.downStation = section.getUpStation();
    }

    private boolean hasEqualStations(Section section) {
        return hasEqualUpStation(section) && hasEqualDownStation(section);
    }

    private boolean hasEqualUpStation(Section section) {
        return this.upStation.equals(section.getUpStation());
    }

    private boolean hasEqualDownStation(Section section) {
        return this.downStation.equals(section.getDownStation());
    }

    public void checkStationsDuplicate(Section section) {
        if (hasEqualStations(section)) {
            throw new SectionAlreadyExistInTheLineException("등록하려는 구간이 이미 노선에 존재합니다.");
        }
    }

    private void checkInnerDistance(int distance) {
        if (this.distance <= distance) {
            throw new SectionDistanceExceededException("역 사이에 추가하려는 구간의 거리는 원래 구간 거리보다 작아야 합니다.");
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return distance == section.distance && Objects.equals(id, section.id)
            && Objects.equals(line, section.line) && Objects
            .equals(upStation, section.upStation) && Objects
            .equals(downStation, section.downStation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
