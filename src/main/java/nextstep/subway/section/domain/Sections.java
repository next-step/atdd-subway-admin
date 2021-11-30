package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;


@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addToSections(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> results = new ArrayList<>();

        Map<Station, Station> sortedStations = doCacheWithUpStations();
        Station upStation = getFirstStation(sortedStations);

        while (upStation != null) {
            results.add(upStation);
            upStation = sortedStations.get(upStation);
        }

        return results;
    }

    private Station getFirstStation(Map<Station, Station> sortedStations) {
        Map<Station, Station> cacheWithDownStations = doCacheWithDownStations();
        return sortedStations.keySet().stream()
                .filter(upStation -> {
                    Station targetStation = cacheWithDownStations.get(upStation);
                    return Objects.isNull(targetStation);
                })
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Map<Station, Station> doCacheWithUpStations() {
        return this.sections.stream()
                .collect(toMap(Section::getUpStation, Section::getDownStation));
    }

    private Map<Station, Station> doCacheWithDownStations() {
        return this.sections.stream()
                .collect(toMap(Section::getDownStation, Section::getUpStation));
    }
}
