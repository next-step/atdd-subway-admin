package nextstep.subway.station_section;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Embeddable
public class StationSectionsByStation {
    @OneToMany(mappedBy = "station")
    private Set<StationSection> stationSections = new HashSet<>();

    protected StationSectionsByStation() {}

    public StationSectionsByStation(Set<StationSection> stationSections) {
        this.stationSections = stationSections;
    }

    public void add(StationSection stationSection) {
        stationSections.add(stationSection);
    }
}
