package nextstep.subway.line.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import nextstep.subway.common.exception.InvalidDataException;
import nextstep.subway.station.domain.Station;
import org.springframework.util.Assert;

@Entity
@Table(indexes = @Index(name = "idx_section_index", columnList = "index"))
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "up_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_up_station"))
    private Station upStation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "down_station_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_down_station"))
    private Station downStation;

    @Embedded
    private Distance distance;

    @Column(nullable = false)
    private int index;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false, foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private Line line;

    protected Section() {
    }

    private Section(Station upStation, Station downStation, Distance distance) {
        validate(upStation, downStation, distance);
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section of(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public void remove(Section section) {
        Assert.notNull(section, "removed section must not be null");
        cutSection(section);
        minusDistance(section.distance);
    }

    List<Station> stations() {
        return Arrays.asList(upStation, downStation);
    }

    void setIndex(int index) {
        this.index = index;
    }

    Station upStation() {
        return upStation;
    }

    Station downStation() {
        return downStation;
    }

    void setLine(Line line) {
        this.line = line;
    }

    private void validate(Station upStation, Station downStation, Distance distance) {
        Assert.notNull(upStation, "'upStation' must not be null");
        Assert.notNull(downStation, "'downStation' must not be null");
        Assert.notNull(distance, "'distance' must not be null");
        Assert.isTrue(!upStation.equals(downStation),
            String.format("upStation(%s) and downStation(%s) must not equal",
                upStation, downStation));
    }

    private void cutSection(Section section) {
        validateSectionStation(section);
        if (this.upStation.equals(section.upStation)) {
            upStation = section.downStation;
            return;
        }
        downStation = section.upStation;
    }

    private void validateSectionStation(Section section) {
        if (isNotEqualStationOnlyOneDirection(section)) {
            throw new InvalidDataException(
                String.format("%s can not be removed from %s", this, section));
        }
    }

    private boolean isNotEqualStationOnlyOneDirection(Section section) {
        return this.upStation.equals(section.upStation) ==
            this.downStation.equals(section.downStation);
    }

    private void minusDistance(Distance distance) {
        validateSubtractDistance(distance);
        this.distance = this.distance.subtract(distance);
    }

    private void validateSubtractDistance(Distance distance) {
        if (distance.moreThan(this.distance)) {
            throw new InvalidDataException(
                String.format("removed section distance(%s) must be less than %s",
                    distance, this.distance));
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, upStation, downStation, distance);
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
        return Objects.equals(id, section.id) && Objects
            .equals(upStation, section.upStation) && Objects
            .equals(downStation, section.downStation) && Objects
            .equals(distance, section.distance);
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
