package nextstep.subway.domain;

import javax.persistence.*;
import java.util.Objects;


@Embeddable
public class Section {
    private static final Long ZERO = 0L;

    @ManyToOne
    @JoinColumn(name = "UPSTATION_ID")
    private Station upStation;

    @ManyToOne
    @JoinColumn(name = "DOWNSTATION_ID")
    private Station downStation;

    @Embedded
    @Column(nullable = false)
    private Distance distance;


    protected Section() {
    }

    public Section(final Long distance) {
        this(new Station(), new Station(), distance);
    }

    public Section(final Station upStation, final Station downStation, final Long distance) {
        this(upStation, downStation, new Distance(distance));
    }

    public Section(final Station upStation, final Station downStation, final Distance distance) {
        validation(upStation, downStation);
        this.upStation = Objects.nonNull(upStation) ? upStation : downStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section updatable(final Section section) {
        if (isSameUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("already registered this section");
        }
        if (section.isSameDownStation(this.upStation) || section.isSameUpStation(this.downStation)) {
            return section;
        }
        if (isZero(section.getDistance())) {
            throw new IllegalArgumentException("invalid distance");
        }
        return insertMiddle(section);
    }

    private boolean isSameUpStationAndDownStation(final Section section) {
        return section.isSameUpStation(this.upStation) && section.isSameDownStation(this.downStation) ||
                section.isSameDownStation(this.upStation) && section.isSameUpStation(this.downStation);
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public Section updateUpStationBy(final Station upStation) {
        this.upStation = upStation;
        return this;
    }

    public Section updateDownStationBy(final Station downStation) {
        this.downStation = downStation;
        return this;
    }

    private Section insertMiddle(final Section destination) {
        if (destination.isSameUpStation(upStation)) {
            return updateUpStationWithDistanceBy(destination);
        }
        if (destination.isSameDownStation(downStation)) {
            return updateDownStationWithDistanceBy(destination);
        }
        throw new IllegalArgumentException("no match station");
    }

    private Section updateDownStationWithDistanceBy(Section destination) {
        this.downStation = destination.getUpStation();
        this.distance = destination.subtractDistanceBy(distance);
        return destination;
    }

    private Section updateUpStationWithDistanceBy(Section destination) {
        this.upStation = destination.getDownStation();
        this.distance = destination.subtractDistanceBy(distance);
        return destination;
    }

    private boolean isZero(final Distance distance) {
        return Objects.equals(this.distance.subtract(distance), new Distance(ZERO));
    }

    private Distance subtractDistanceBy(final Distance source) {
        return source.subtract(distance);
    }

    private boolean isSameUpStation(final Station station) {
        return Objects.equals(station, upStation);
    }

    private boolean isSameDownStation(final Station station) {
        return Objects.equals(station, downStation);
    }

    private void validation(final Station upStation, final Station downStation) {
        if (Objects.isNull(upStation) && Objects.isNull(downStation)) {
            throw new IllegalArgumentException("Either upStation or downStation must have a value");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(upStation, downStation, distance);
    }

}
