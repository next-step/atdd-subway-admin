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
import nextstep.subway.common.Messages;
import nextstep.subway.exception.BusinessException;
import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;

@Entity
public class Section extends BaseEntity implements Comparable<Section> {

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

    private Section(Distance distance, SectionType sectionType, Station station,
        Station linkStation, Line line) {
        this.distance = distance;
        this.sectionType = sectionType;
        setStation(station);
        setLinkStation(linkStation);
        setLine(line);
    }

    public static Section fromDownSection(Station station, Line line) {
        return new Section(Distance.createDownDistance(), SectionType.DOWN, station, null, line);
    }

    public static Section ofUpSection(Distance distance, Station station, Station linkStation,
        Line line) {
        return new Section(distance, SectionType.UP, station, linkStation, line);
    }

    public static Section ofMiddleSection(Distance distance, Station station, Station linkStation,
        Line line) {
        return new Section(distance, SectionType.MIDDLE, station, linkStation, line);
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
        if (!line.containsSection(this)) {
            line.addSection(this);
        }
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        if (station == null) {
            throw new BusinessException(Messages.SECTION_REQUIRED_STATION.getValues());
        }
        this.station = station;
    }

    public Station getLinkStation() {
        return linkStation;
    }

    public void setLinkStation(Station station) {
        if (station == null) {
            return;
        }
        this.linkStation = station;
    }

    public boolean isDownStation() {
        if (this.sectionType.equals(SectionType.DOWN)) {
            return true;
        }
        return false;
    }

    public boolean isUpStation() {
        if (this.sectionType.equals(SectionType.UP)) {
            return true;
        }
        return false;
    }

    public Distance calculateDistance(Distance distance) {
        Integer calcDistance = this.distance.minus(distance);
        return Distance.valueOf(calcDistance);
    }

    public boolean hasLinkStation(Station linkStation) {
        if (this.linkStation.equals(linkStation)) {
            return true;
        }
        return false;
    }

    public boolean equalsLine(Line line) {
        return this.line.equals(line);
    }

    public boolean hasStation(Station station) {
        return this.station.equals(station);
    }

    public Section update(SectionType sectionType) {
        return update(this.distance, sectionType, this.linkStation);
    }

    public Section update(Distance distance, Station linkStation) {
        return update(distance, this.sectionType, linkStation);
    }

    public Section update(Distance distance, SectionType sectionType, Station linkStation) {
        this.distance = distance;
        this.sectionType = sectionType;
        this.linkStation = linkStation;
        return this;
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

    @Override
    public String toString() {
        return "Section{" +
            "id=" + id +
            ", distance=" + distance +
            ", sectionType=" + sectionType +
            ", station=" + station +
            ", linkStation=" + linkStation +
            ", line=" + line.getName() +
            '}';
    }


}
