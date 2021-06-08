package nextstep.subway.station_section;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class StationSection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "station_id", foreignKey = @ForeignKey(name = "fk_station_section"))
    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Station station;

    @JoinColumn(name = "section_id", foreignKey = @ForeignKey(name = "fk_section_station"))
    @ManyToOne(fetch = FetchType.LAZY)
    private Section section;

    @Enumerated(value = EnumType.STRING)
    private StationType stationType;

    protected StationSection() {}

    private StationSection(Station station, Section section, StationType stationType) {
        this.station = station;
        this.section = section;
        this.stationType = stationType;
    }

    public static StationSection create(Station station, Section section, StationType stationType) {
        validate(station, section, stationType);
        StationSection stationSection = new StationSection(station, section, stationType);
        stationSection.setStation(station);
        stationSection.setSection(section);
        return stationSection;
    }

    private static void validate(Station station, Section section, StationType stationType) {
        if (Objects.isNull(station)) {
            throw new NullPointerException("역이 존재하지 않습니다.");
        }

        if (Objects.isNull(section)) {
            throw new NullPointerException("구간이 존재하지 않습니다.");
        }

        if (Objects.isNull(stationType)) {
            throw new NullPointerException("역타입이 존재하지 않습니다.");
        }
    }

    public void setStation(Station station) {
        this.station = station;
        station.addStationSection(this);
    }

    public void setSection(Section section) {
        this.section = section;
        section.addStationSection(this);
    }

    public boolean isUpStationType() {
        return stationType == StationType.UP;
    }

    public boolean isDownStationType() {
        return stationType == StationType.DOWN;
    }

    public boolean isUpSectionType() {
        return stationType == StationType.DOWN;
    }

    public boolean isDownSectionType() {
        return stationType == StationType.UP;
    }

    public String stationName() {
        return station.getName();
    }

    @Override
    public String toString() {
        return "StationSection{" +
                "id=" + id +
                ", station name=" + station.getName() +
                ", section distance=" + section.distance() +
                ", stationType=" + stationType +
                '}';
    }

    public Long getId() {
        return id;
    }

    public Station getStation() {
        return station;
    }

    public Section getSection() {
        return section;
    }
}
