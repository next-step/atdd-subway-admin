package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Table(name = "section")
@Entity
public class Section extends BaseEntity {
    private static final String NEW_SECTION_MUST_SHORTER_THAN_EXIST_SECTION = "새로 등록되는 구간 길이가 기존 역 사이 길이보다 크거나 같을 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(optional = false)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.from(distance);
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
        return distance.getDistance();
    }

    public List<Station> getUpAndDownStation() {
        return Stream.of(upStation, downStation)
                .collect(toList());
    }

    public boolean isSameEdges(Section other) {
        return this.upStation.equals(other.getUpStation()) && this.downStation.equals(other.getDownStation());
    }

    public boolean isSameDownStation(Section other) {
        return this.downStation.equals(other.getDownStation());
    }

    private boolean isSameUpStation(Section other) {
        return this.upStation.equals(other.getUpStation());
    }

    public boolean isAfter(Section other) {
        return this.upStation.equals(other.getDownStation());
    }

    public List<Section> insertNewSection(Section newSection) {
        if (isSameUpStation(newSection)) {
            updateUpStation(newSection);
            return Stream.of(this, newSection).collect(toList());
        }
        if (isSameDownStation(newSection)) {
            updateDownStation(newSection);
            return Stream.of(this, newSection).collect(toList());
        }
        return Stream.of(this, newSection).collect(toList());
    }

    private void updateDownStation(Section newSection) {
        int distanceDiff = distanceDiffWithNewSection(newSection.distance);
        this.downStation = newSection.upStation;
        this.distance = Distance.from(distanceDiff);
    }

    private void updateUpStation(Section newSection) {
        int distanceDiff = distanceDiffWithNewSection(newSection.distance);
        this.upStation = newSection.downStation;
        this.distance = Distance.from(distanceDiff);
    }

    private int distanceDiffWithNewSection(Distance newSectionDistacne) {
        int distanceDiff = this.distance.getDistance() - newSectionDistacne.getDistance();
        if (distanceDiff <= 0) {
            throw new IllegalArgumentException(NEW_SECTION_MUST_SHORTER_THAN_EXIST_SECTION);
        }
        return distanceDiff;
    }

    public void setLine(Line line) {
        this.line = line;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(line, section.line) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
