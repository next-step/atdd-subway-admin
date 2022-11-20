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

    private static final int MIN_SECTION_COUNT = 2;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void add(Station preStation, Station station, Integer distance) {
        this.sections.add(new Section(preStation, station, distance));
    }

    public void addSection(Station preStation, Station station, Integer distance) {
        if(isNotExistsFirstStation()){
            addFirstStation(preStation);
        }

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

    private void addFirstStation(Station preStation) {
        add(null, preStation, 0);
    }

    private boolean isNotExistsFirstStation() {
        return sections.size() < MIN_SECTION_COUNT;
    }

    public void deleteSectionByStation(Station station) {
        Section findSection = findSectionByStation(station);

        sections.stream()
                .filter(section -> station.equals(section.getPreStation()))
                .findFirst()
                .ifPresent(section -> section.linkPreSectionByDelete(findSection));

        sections.remove(findSection);
    }

    private Section findSectionByStation(Station station) {
        Map<Station, Section> toMap = getSectionsToMap();
        validateNotIncludeSection(station, toMap);
        validateLastSection();
        return toMap.get(station);

    }

    private void validateNotIncludeSection(Station station, Map<Station, Section> toMap) {
        if (!toMap.containsKey(station)) {
            throw new IllegalArgumentException("구간 내에 존재하지 않는 역입니다.");
        }
    }

    private void validateLastSection() {
        if (sections.size() == MIN_SECTION_COUNT) {
            throw new IllegalArgumentException("마지막 구간이므로 삭제할 수 없습니다.");
        }
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
        Section firstSection = findFirstSection().orElse(null);

        List<Section> orders = new ArrayList<>();
        while (firstSection != null) {
            Section tmp = firstSection;
            orders.add(tmp);
            firstSection = map.get(tmp.getStation());
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
