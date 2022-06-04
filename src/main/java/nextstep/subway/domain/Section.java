package nextstep.subway.domain;

import java.util.Objects;


public class Section {
    private Station upStation;
    private Station downStation;
    private Distance distance;

    protected Section() {
    }

    public Section(final Station upStation, final Station downStation, final Long distance) {
        this(upStation, downStation, new Distance(distance));
    }

    public Section(final Station upStation, final Station downStation, final Distance distance) {
        validation(upStation, downStation, distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section updatable(final Section section) {
        if (isSameUpStationAndDownStation(section)) {
            throw new IllegalArgumentException("already register this station");
        }
        if (section.isSameDownStation(this.upStation) || section.isSameUpStation(this.downStation)) {
            return section;
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

    private Distance subtractDistanceBy(final Distance source) {
        return source.subtract(distance);
    }

    private boolean isSameUpStation(final Station station) {
        return Objects.equals(station, upStation);
    }

    private boolean isSameDownStation(final Station station) {
        return Objects.equals(station, downStation);
    }

    private void validation(final Station upStation, final Station downStation, final Distance distance) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
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
