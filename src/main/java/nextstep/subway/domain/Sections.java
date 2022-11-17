package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void add(Station preStation, Station station, Integer distance) {
        this.sections.add(new Section(preStation, station, distance));
    }

    public void addSection(Station preStation, Station station, Integer distance) {
        Map<Station, Section> toMap = getSectionsToMap();
        validateStation(preStation, station, toMap);

        if (toMap.containsKey(preStation)) {
            updateSectionForPreStation(preStation, station, distance);
        }

        if (toMap.containsKey(station)) {
            updateSectionForStation(preStation, station, distance);
        }

        add(preStation, station, distance);
    }

    private void updateSectionForStation(Station preStation, Station station, Integer distance) {
        this.sections.stream()
                .filter(section -> station.equals(section.getStation()))
                .findFirst()
                .ifPresent(section -> section.updateSection(section.getPreStation(), preStation, distance));
    }

    private void updateSectionForPreStation(Station preStation, Station station, Integer distance) {
        this.sections.stream()
                .filter(section -> preStation.equals(section.getPreStation()))
                .findFirst()
                .ifPresent(section -> section.updateSection(station, section.getStation(), distance));
    }

    private void validateStation(Station preStation, Station station, Map<Station, Section> toMap) {
        validateAllIncludeStation(preStation, station, toMap);
        validateNotIncludeStation(preStation, station, toMap);
    }

    private void validateAllIncludeStation(Station preStation, Station station, Map<Station, Section> toMap) {
        if (toMap.containsKey(preStation) && toMap.containsKey(station)) {
            throw new IllegalArgumentException("시작/도착 역이 이미 존재합니다.");
        }
    }

    private void validateNotIncludeStation(Station preStation, Station station, Map<Station, Section> toMap) {
        if (!toMap.containsKey(preStation) && !toMap.containsKey(station)) {
            throw new IllegalArgumentException("시작/도착 역이 모두 존재하지 않습니다.");
        }
    }

    private Map<Station, Section> getSectionsToMap() {
        return sections.stream().collect(Collectors.toMap(
                Section::getStation, section -> section
        ));
    }


    public List<Section> getOrderStations() {
        Map<Station, Section> map = getSectionsToMapByPreStation();
        Section section1 = findFirstSection().orElse(null);

        List<Section> orders = new ArrayList<>();
        while (section1 != null) {
            Section tmp = section1;
            orders.add(tmp);
            section1 = map.get(tmp.getStation());
        }

        return orders;
    }

    private Map<Station, Section> getSectionsToMapByPreStation() {
        return sections.stream().collect(Collectors.toMap(
                Section::getPreStation, section -> section
        ));
    }

    private Optional<Section> findFirstSection() {
        return this.sections.stream()
                .filter(section -> section.getPreStation() == null)
                .findAny();
    }

    public List<Section> getSections() {
        return sections;
    }
}
