package nextstep.subway.line.domain;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.exception.InvalidSectionException;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    private Station downStation;

    private int distance;

    protected Section() {
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        validateArguments(upStation, downStation, distance);

        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(Station upStation, Station downStation, int distance) {
        this(null, upStation, downStation, distance);
    }

    private void validateArguments(Station upStation, Station downStation, int distance) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new IllegalArgumentException("양 끝 Station이 반드시 존재해야 합니다.");
        }

        if (distance <= 0) {
            throw new IllegalArgumentException("거리값은 반드시 0보다 커야 합니다.");
        }
    }

    public Station upStation() {
        return upStation;
    }

    public Station downStation() {
        return downStation;
    }

    public int distance() {
        return distance;
    }

    public boolean contains(Station station) {
        return upStation == station
                || downStation == station;
    }

    public boolean isLongerThan(Section section) {
        return this.distance > section.distance;
    }

    public boolean isMergeableWith(Section section) {
        return upStation == section.downStation
                ^ downStation == section.upStation;
    }

    public Section mergeWith(Section section) {
        validateMergeable(section);

        if (this.upStation == section.downStation) {
            return new Section(this.line,
                section.upStation, this.downStation, this.distance + section.distance);
        }

        return new Section(this.line,
            this.upStation, section.downStation, this.distance + section.distance);
    }

    private void validateMergeable(Section section) {
        if (!this.isMergeableWith(section)) {
            throw new InvalidSectionException("병합 조건을 만족하지 않습니다.");
        }
    }

    public boolean matchesOnlyOneEndOf(Section section) {
        return upStation == section.upStation
                ^ downStation == section.downStation;
    }

    public Section shiftedBy(Section section) {
        validateReducible(section);

        if (this.upStation == section.upStation) {
            return new Section(this.line,
                section.downStation, this.downStation, this.distance - section.distance);
        }

        return new Section(this.line,
            this.upStation, section.upStation, this.distance - section.distance);
    }

    private void validateReducible(Section section) {
        if (!this.matchesOnlyOneEndOf(section)) {
            throw new InvalidSectionException("하나의 종단점만 일치해야 합니다.");
        }

        if (!this.isLongerThan(section)) {
            throw new InvalidSectionException("0 이하로 축소할 수 없습니다.");
        }
    }

    public void setLine(Line line) {
        validateLine(line);
        this.line = line;
    }

    private void validateLine(Line line) {
        if (Objects.isNull(line)) {
            throw new IllegalArgumentException("소속 노선은 null 이 될 수 없습니다.");
        }

        if (!Objects.isNull(this.line)) {
            throw new InvalidSectionException("소속 노선을 재 정의 할 수는 없습니다.");
        }

        if (!line.contains(this)) {
            throw new InvalidSectionException("해당 노선과의 연관관계가 확인되지 않습니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
