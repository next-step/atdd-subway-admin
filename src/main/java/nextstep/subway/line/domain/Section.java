package nextstep.subway.line.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "up_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_section_to_up_station")
    )
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "down_station_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_section_to_down_station")
    )
    private Station downStation;

    @Column(name = "distance")
    private int distance;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "line_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_section_to_line")
    )
    private Line line;

    protected Section() {
    }

    public Section(
        final Long id,
        final Station upStation,
        final Station downStation,
        final int distance
    ) {
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(
        final Station upStation,
        final Station downStation,
        final int distance
    ) {
        this(null, upStation, downStation, distance);
    }

    public void attachToLine(final Line line) {
        this.line = line;
    }

    public void merge(final Section another) {
        validateMiddleStation(another);
        downStation = another.getDownStation();
        distance += another.getDistance();
    }

    private void validateMiddleStation(final Section another) {
        if (!downStation.equals(another.upStation)) {
            throw new IllegalArgumentException("중간역이 다를 경우 구간을 합칠 수 없습니다.");
        }
    }

    public void adjustUpStation(final Section newSection) {
        validateNewSection(newSection);
        upStation = newSection.getDownStation();
        adjustDistance(newSection.getDistance());
    }

    public void adjustDownStation(final Section newSection) {
        validateNewSection(newSection);
        downStation = newSection.getUpStation();
        adjustDistance(newSection.getDistance());
    }

    private void validateNewSection(final Section section) {
        if (equals(section)) {
            throw new IllegalArgumentException("이미 노선에 등록된 구간을 추가할 수 없습니다.");
        }
        if (distance <= section.getDistance()) {
            throw new IllegalArgumentException("새 구간의 역 사이 길이가 기존 역 사이 길이보다 크거나 같으면 등록 할 수 없습니다.");
        }
    }

    private void adjustDistance(final int distance) {
        this.distance -= distance;
    }

    public boolean hasStation(final Station station) {
        return upStation.equals(station) || downStation.equals(station);
    }

    public Long getId() {
        return id;
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Section section = (Section) o;
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
