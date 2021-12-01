package nextstep.subway.section.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.common.ErrorCode;
import nextstep.subway.exception.NotAcceptableApiException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.NoSuchElementException;
import java.util.Objects;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    private int distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, Line line, int distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.line = line;
        this.distance = distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Line getLine() {
        return line;
    }

    public int getDistance() {
        return distance;
    }

    public static Section of(Line line, Station upStation, Station downStation, int distance) {
        return new Section(upStation, downStation, line, distance);
    }

    public void updateUpStation(Section newSection) {
        validateNewSection(newSection);
        upStation = newSection.getDownStation();
        distance = distance - newSection.getDistance();
    }

    public void updateDownStation(Section newSection) {
        validateNewSection(newSection);
        downStation = newSection.getUpStation();
        distance = distance - newSection.getDistance();
    }

    public boolean hasNexSection() {
        return line.getSections().stream()
                .anyMatch(section -> section.upStation.equals(downStation));
    }

    public Section getNextSection() {
        return line.getSections().stream()
                .filter(section -> section.upStation.equals(downStation))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(String.format("다음 구간이 없습니다. (sectionId: %d)", id)));
    }

    public boolean isEqualUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean isEqualDownStation(Station station) {
        return downStation.equals(station);
    }

    private void validateNewSection(Section newSection) {
        if (newSection == null) {
            throw new IllegalArgumentException("Section은 null일 수 없습니다.");
        }
        if (distance <= newSection.getDistance()) {
            throw new NotAcceptableApiException(ErrorCode.INVALID_SECTION_DISTANCE);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Section)) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
