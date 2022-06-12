package nextstep.subway.domain;

import com.google.common.collect.Sets;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.generic.domain.distance.Distance;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Column(name = "distance")
    private Distance distance;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final int distance) {
        this.upStation = Objects.requireNonNull(upStation);
        this.downStation = Objects.requireNonNull(downStation);
        this.distance = Distance.valueOf(distance);
    }

    public Set<Station> getStations() {
        return Sets.newHashSet(upStation, downStation);
    }

    public boolean intersects(final Section section) {
        return isStandardUpStation(section) || isStandardDownStation(section);
    }

    private boolean isStandardDownStation(final Section section) {
        return this.downStation.equals(section.downStation);
    }

    private boolean isStandardUpStation(final Section section) {
        return this.upStation.equals(section.upStation);
    }

    public void calculate(final Section section) {
        validateDistance(section);
        if (isStandardUpStation(section)) {
            this.upStation = section.downStation;
            this.distance = distance.minus(section.distance);
            return;
        }

        this.downStation = section.upStation;
        this.distance = distance.minus(section.distance);
    }

    private void validateDistance(final Section section) {
        if (section.distance.isGreaterThanOrEqualTo(this.distance)) {
            throw new SectionException("역과 역사이에 등록시에 거리는 기존보다 크거나 같을 수 없습니다.");
        }
    }

    public boolean hasDownStation(final Station station) {
        return this.downStation.equalsId(station);
    }

    public boolean hasUpStation(final Station station) {
        return this.upStation.equalsId(station);
    }

    public boolean equalsStations(final Section section) {
        return hasUpStation(section.upStation) && hasDownStation(section.downStation);
    }

    public Distance getDistance() {
        return distance;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Section)) {
            return false;
        }
        final Section that = (Section) o;
        return Objects.equals(id, that.id) && Objects.equals(upStation, that.upStation)
                && Objects.equals(downStation, that.downStation) && Objects.equals(distance,
                that.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
