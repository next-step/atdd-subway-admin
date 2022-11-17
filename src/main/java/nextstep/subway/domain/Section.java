package nextstep.subway.domain;

import java.util.Arrays;
import java.util.Collections;
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
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Long id, Line line, Station upStation, Station downStation, long distance) {
        this.id = id;
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = Distance.of(distance);
    }

    public Section(Long id, Station upStation, Station downStation, long distance) {
        this(id, null, upStation, downStation, distance);
    }

    public Section(Line line, Station upStation, Station downStation, long distance) {
        this(null, line, upStation, downStation, distance);
    }

    public Section(Station upStation, Station downStation, long distance) {
        this(null, null, upStation, downStation, distance);
    }

    public void modify(Section section) {
        modifyUpStation(section);
        modifyDownStation(section);
    }

    private void modifyUpStation(Section section) {
        if (!Objects.equals(this.upStation, section.upStation)) {
            return;
        }
        this.upStation = section.downStation;
        this.distance = this.distance.sub(section.distance);
    }

    private void modifyDownStation(Section section) {
        if (!Objects.equals(this.downStation, section.downStation)) {
            return;
        }
        this.downStation = section.upStation;
        this.distance = this.distance.sub(section.distance);
    }

    public List<Station> getStations() {
        return Collections.unmodifiableList(
            Arrays.asList(this.upStation, this.downStation)
        );
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

    public Distance getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Section section = (Section)o;
        return Objects.equals(getId(), section.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
