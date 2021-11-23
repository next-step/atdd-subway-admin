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
import nextstep.subway.line.common.Constants;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity implements Comparable<Section>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Distance distance;

    @Enumerated(EnumType.STRING)
    private SectionType sectionType;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @ManyToOne
    @JoinColumn(name = "link_station_id")
    private Station linkStation;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    protected Section() {
    }

    private Section(Distance distance, SectionType sectionType, Station station, Station linkStation) {
        this.distance = distance;
        this.sectionType = sectionType;
        setStation(station);
        setLinkStation(linkStation);
    }

    public static Section fromDownStation(Station station) {
        return new Section(Distance.createDownDistance(), SectionType.DOWN, station, null);
    }

    public static Section ofUpStation(Distance distance, Station station, Station linkStation) {
        return new Section(distance, SectionType.UP, station, linkStation);
    }

    public static Section ofMiddleStation(Distance distance, Station station, Station linkStation) {
        return new Section(distance, SectionType.MIDDLE, station, linkStation);
    }

    /**
     * 연관관계 편의 메서드
     *
     * @param line
     */
    public void setLine(Line line) {
        if (this.line != null) {
            this.line.removeSection(this);
        }

        this.line = line;
        line.addSection(this);
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        if (station == null) {
            throw new IllegalArgumentException("역정보는 필수입니다.");
        }
        this.station = station;
    }

    public void setLinkStation(Station station) {
        if (station == null) {
            return;
        }
        this.linkStation = station;
    }

    @Override
    public int compareTo(Section o) {

        if (sectionType.equals(SectionType.UP)) {
            return -1;
        }

        if (linkStation == null) {
            return 1;
        }

        if (o.linkStation == null) {
            return -1;
        }

        if (linkStation.equals(o.station)) {
            return -1;
        }

        if (station.equals(o.linkStation)) {
            return 1;
        }

        return 0;
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
        return Objects.hash(id);
    }
}
