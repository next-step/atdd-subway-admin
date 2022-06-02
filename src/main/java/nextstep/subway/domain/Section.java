package nextstep.subway.domain;

import java.util.Objects;

public class Section {
    private static final Long ZERO = 0L;
    private Long id;
    private Station upStation;
    private Station downStation;
    private Distance distance;

    public Section(final long id, final Station upStation, final Station downStation, final Distance distance) {
        validation(upStation, downStation, distance);
        this.id = id;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public Section(final long id, final Station upStation, final Station downStation, final Long distance) {
        this(id, upStation, downStation, new Distance(distance));
    }

    public Section updatable(Section section) {
        validation(section);
        if (section.isSameDownStation(this.upStation) || section.isSameUpStation(this.downStation)) {
            return section;
        }
        return insertMiddle(section);
    }

    private Section insertMiddle(final Section section) {
        if (section.isSameUpStation(upStation)) {

        }
        if (section.isSameDownStation(downStation)) {

        }
        throw new IllegalArgumentException("no match station");
    }

    private boolean isSameUpStation(final Station station) {
        return Objects.equals(station, upStation);
    }

    private boolean isSameDownStation(final Station station) {
        return Objects.equals(station, downStation);
    }


    private void validation(final Section section) {
        if (!section.isBigDistance(this.distance)) {
            throw new IllegalArgumentException("invalid distance");
        }
    }
    private boolean isBigDistance(Distance distance) {
        return this.distance.isBig(distance);
    }

    private void validation(final Station upStation, final Station downStation, final Distance distance) {
        if (Objects.isNull(upStation) || Objects.isNull(downStation)) {
            throw new IllegalArgumentException("Either upStation or downStation must have a value");
        }
        if (!distance.isBig(new Distance(ZERO))) {
            throw new IllegalArgumentException("distance must have more zero");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && Objects.equals(upStation, section.upStation) && Objects.equals(downStation, section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
    }

}
