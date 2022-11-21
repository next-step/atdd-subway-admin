package nextstep.subway.section;

import nextstep.subway.station.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;
import java.util.stream.Collectors;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    private final List<Section> sections = new ArrayList<>();

    public Sections() { }

    public void add(Section newSection) {
        if (sections.size() == 0) {
            sections.add(newSection);
            return;
        }
        if (addUpStation(newSection)) {
            return;
        }
        if (addDownStation(newSection)) {
            return;
        }
    }

    private boolean addUpStation(Section newSection) {
        Station upStation = sections.get(0).getUpStation();
        if (newSection.getDownStation().equals(upStation)) {
            sort().forEach(Section::increaseNumber);
            sections.add(newSection);
            return true;
        }
        return false;
    }

    private boolean addDownStation(Section newSection) {
        Station downStation = sections.get(sections.size() - 1).getDownStation();
        if (newSection.getUpStation().equals(downStation)) {
            newSection.addNumber(sections.size());
            sections.add(newSection);
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getStations() {
        if (sections.size() == 0) {
            return Collections.emptyList();
        }
        List<Section> sort = sort();
        List<Map<String, Object>> stations = sort.stream()
                .map(section -> section.getUpStation().toMapForOpen())
                .collect(Collectors.toList());
        Section lastSection = sort.get(sort.size() - 1);
        stations.add(lastSection.getDownStation().toMapForOpen());
        return stations;
    }

    private List<Section> sort() {
        return sections.stream()
                .sorted(Comparator.comparingInt(Section::getNumber))
                .collect(Collectors.toList());
    }
}
