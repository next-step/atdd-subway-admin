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

    private static final int MIN_SECTION_COUNT = 1;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void add(Station preStation, Station station, Integer distance) {
        this.sections.add(new Section(preStation, station, distance));
    }

    public void addSection(Station preStation, Station station, Integer distance) {
        boolean isPresentPreStation = isExistsStation(preStation);
        boolean isPresentStation = isExistsStation(station);
        validateStation(isPresentPreStation, isPresentStation);

        if (isPresentPreStation) {
            updateSectionForPreStation(preStation, station, distance);
        }

        if (isPresentStation) {
            updateSectionForStation(preStation, station, distance);
        }

        add(preStation, station, distance);
    }

    private void validateStation(boolean isPresentPreStation, boolean isPresentStation) {
        if (isPresentPreStation && isPresentStation) {
            throw new IllegalArgumentException("시작/도착 역이 이미 존재합니다.");
        }

        if (!isPresentPreStation && !isPresentStation) {
            throw new IllegalArgumentException("시작/도착 역이 모두 존재하지 않습니다.");
        }
    }

    private boolean isExistsStation(Station station) {
        return getSectionsToMap().containsKey(station) || getSectionsToMapByPreStation().containsKey(station);
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

    public void deleteSectionByStation(Station station) {
        validateNotIncludeSection(station);
        validateLastSection();
        Optional<Section> sectionByStation = Optional.ofNullable(getSectionsToMap().get(station));
        Optional<Section> sectionByPreStation = Optional.ofNullable(getSectionsToMapByPreStation().get(station));

        if(sectionByPreStation.isPresent() && sectionByStation.isPresent()){
            createNewSection(sectionByStation.get(), sectionByPreStation.get());
        }

        sectionByStation.ifPresent(section -> sections.remove(section));
        sectionByPreStation.ifPresent(section -> sections.remove(section));
    }

    private void validateNotIncludeSection(Station station) {
        if (!isExistsStation(station)) {
            throw new IllegalArgumentException("구간 내에 존재하지 않는 역입니다.");
        }
    }

    private void validateLastSection() {
        if (sections.size() == MIN_SECTION_COUNT) {
            throw new IllegalArgumentException("마지막 구간이므로 삭제할 수 없습니다.");
        }
    }

    private void createNewSection(Section sectionByStation, Section sectionByPreStation) {
        Station newPreStation = sectionByStation.getPreStation();
        Station newStation = sectionByPreStation.getStation();
        int newDistance = sectionByStation.getDistance() + sectionByPreStation.getDistance();

        add(newPreStation, newStation, newDistance);
    }

    public List<Station> getOrderStations() {
        Map<Station, Section> sectionsMapByPreStation = getSectionsToMapByPreStation();
        Section firstSection = findFirstSection().orElse(null);

        List<Station> orders = new ArrayList<>();
        orders.add(firstSection.getPreStation());
        orders.addAll(getStations(sectionsMapByPreStation, firstSection));

        return orders;
    }

    private List<Station> getStations(Map<Station, Section> sectionsMapByPreStation, Section firstSection) {
        List<Station> orders = new ArrayList<>();
        while (firstSection != null) {
            Section tmp = firstSection;
            orders.add(tmp.getStation());
            firstSection = sectionsMapByPreStation.get(tmp.getStation());
        }
        return new ArrayList<>(orders);
    }

    private Optional<Section> findFirstSection() {
        Map<Station, Section> sectionsToMap = getSectionsToMap();
        return this.sections.stream()
                .filter(section -> !sectionsToMap.containsKey(section.getPreStation()))
                .findAny();
    }
    private Map<Station, Section> getSectionsToMap() {
        return sections.stream().collect(Collectors.toMap(
                Section::getStation, section -> section
        ));
    }

    private Map<Station, Section> getSectionsToMapByPreStation() {
        return sections.stream().collect(Collectors.toMap(
                Section::getPreStation, section -> section
        ));
    }

    public List<Section> getSections() {
        return sections;
    }
}
