package nextstep.subway.station.domain;

import nextstep.subway.common.BaseEntity;
import nextstep.subway.exception.ValueFormatException;
import nextstep.subway.station_section.StationSection;
import nextstep.subway.station_section.StationSectionsByStation;
import org.apache.logging.log4j.util.Strings;

import javax.persistence.*;
import java.util.HashSet;

@Entity
public class Station extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;

    @Embedded
    private StationSectionsByStation stationSectionsByStation = new StationSectionsByStation(new HashSet<>());

    protected Station() {
    }

    private Station(String name) {
        this.name = name;
    }

    public static Station create(String name) {
        validate(name);
        return new Station(name);
    }

    private static void validate(String name) {
        if (Strings.isBlank(name)) {
            throw new ValueFormatException("역의 이름이 존재하지 않습니다.", "name", name, null);
        }
    }

    public void addStationSection(StationSection stationSection) {
        stationSectionsByStation.add(stationSection);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
