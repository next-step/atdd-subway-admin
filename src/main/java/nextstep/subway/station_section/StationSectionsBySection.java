package nextstep.subway.station_section;

import nextstep.subway.exception.NoSuchDataException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Embeddable
public class StationSectionsBySection {
    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StationSection> stationSections = new HashSet<>();

    protected StationSectionsBySection() {}

    public StationSectionsBySection(Set<StationSection> stationSections) {
        this.stationSections = stationSections;
    }

    public void add(StationSection stationSection) {
        stationSections.add(stationSection);
    }

    public String upStationName() {
        return stationSections.stream()
                .filter(StationSection::isUpStationType)
                .findFirst()
                .map(StationSection::stationName)
                .orElseThrow(() -> new NoSuchDataException("상행역이 존재하지 않습니다.", "upStation", null, null));
    }

    public String downStationName() {
        return stationSections.stream()
                .filter(StationSection::isDownStationType)
                .findFirst()
                .map(StationSection::stationName)
                .orElseThrow(() -> new NoSuchDataException("하행역이 존재하지 않습니다.", "downStation", null, null));
    }

    public List<StationSection> toList() {
        return stationSections.stream().collect(Collectors.toList());
    }

    public int size() {
        return stationSections.size();
    }
}
