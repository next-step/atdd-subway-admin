package nextstep.subway.domain;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id", nullable = false)
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id", nullable = false)
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id", nullable = false)
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() { }

    private Section(Station upStation, Station downStation, Distance distance) {
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public static Section from(Station upStation, Station downStation, Distance distance) {
        return new Section(upStation, downStation, distance);
    }

    public List<Station> stations() {
        return Arrays.asList(upStation, downStation);
    }

    public void addLine(Line line) {
       this.line = line;
    }

    public void reorganize(Section newSection) {
        if (isSameUpStation(newSection)) {
            this.upStation = newSection.downStation;
            this.distance = this.distance.subtract(newSection.distance);
        }
        if (isSameDownStation(newSection)) {
            this.downStation = newSection.upStation;
            this.distance = this.distance.subtract(newSection.distance);
        }
    }

    private boolean isSameUpStation(Section newSection) {
        return this.upStation.equals(newSection.upStation);
    }

    private boolean isSameDownStation(Section newSection) {
        return this.downStation.equals(newSection.downStation);
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
        return Objects.equals(id, section.id) && Objects.equals(line, section.line)
                && Objects.equals(upStation, section.upStation) && Objects.equals(downStation,
                section.downStation) && Objects.equals(distance, section.distance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, line, upStation, downStation, distance);
    }
}
