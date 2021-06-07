package nextstep.subway.station_section;

import nextstep.subway.exception.NoSuchDataException;
import nextstep.subway.section.Section;

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

    public Section downSection() {
        return stationSections.stream()
                .filter(StationSection::isDownSectionType)
                .findFirst()
                .orElseThrow(() -> new NoSuchDataException("하행선 구간이 존재하지 않습니다.", null, null, null))
                .getSection();
    }
}
