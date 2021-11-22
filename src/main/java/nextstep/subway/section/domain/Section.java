package nextstep.subway.section.domain;

import java.util.Objects;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.common.BaseEntity;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity implements Comparable<Section>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @Embedded
    private SectionSortSeq sortSeq;

    @Enumerated(EnumType.STRING)
    private SectionType sectionType;

    @ManyToOne
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    public Section(Integer distance, Integer sortSeq, Station station, SectionType sectionType) {
        this.distance = new Distance(distance);
        this.sortSeq = new SectionSortSeq(sortSeq);
        this.sectionType = sectionType;
        setStation(station);
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public SectionSortSeq getSortSeq() {
        return sortSeq;
    }

    @Override
    public int compareTo(Section o) {
        return sortSeq.compare(o.sortSeq);
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
        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, distance, sortSeq, station, line);
    }
}
