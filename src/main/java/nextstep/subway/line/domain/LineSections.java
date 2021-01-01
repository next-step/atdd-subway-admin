package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;

@Embeddable
public class LineSections {

    private final static int REMOVABLE_SECTION_SIZE = 3;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public LineSections() {
    }

    public LineSections(Section upStationSection, Section downStationSection) {
        this.sections = Arrays.asList(upStationSection, downStationSection);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return this.sections.stream()
            .map(Section::getStation)
            .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        boolean includeUpStationInSection = findByStation(newSection.getPreStation());
        boolean includeDownStationInSection = findByStation(newSection.getStation());

        checkValidStation(includeUpStationInSection, includeDownStationInSection);

        if (includeUpStationInSection) {
            updatePreStationTpNewDownStation(newSection);
        }

        if (includeDownStationInSection) {
            updateStationToNewUpStation(newSection);
        }
        this.sections.add(newSection);
    }

    public void updatePreStationTpNewDownStation(Section newSection) {
        this.sections.stream()
            .filter(section -> section.isPreStationInSection(newSection.getPreStation()))
            .findFirst()
            .ifPresent(section -> section.updatePreStationToAdd(newSection.getStation(), newSection.getDistance()));
    }

    public void updateStationToNewUpStation(Section newSection) {
        this.sections.stream()
            .filter(section -> section.isStationInSection(newSection.getStation()))
            .findFirst()
            .ifPresent(section -> section.updateStationToAdd(newSection.getPreStation(), newSection.getDistance()));
    }

    public void removeSection(Long stationId) {
        checkRemovableSections();

        Section removeSection = this.sections.stream()
            .filter(section -> section.isStationInSection(stationId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("노선에 등록되지 않은 역은 제거할 수 없습니다."));

        this.sections.stream()
            .filter(it -> it.isPreStationInSection(removeSection.getStation()))
            .findFirst()
            .ifPresent(section -> section.updatePreStationToRemove(removeSection.getPreStation(), removeSection.getDistance()));

        this.sections.remove(removeSection);
    }

    public List<Section> getOrderedSections() {
        Optional<Section> preLineStation = sections.stream()
            .filter(it -> it.getPreStation() == null)
            .findFirst();

        List<Section> result = new ArrayList<>();
        while (preLineStation.isPresent()) {
            Section preStation = preLineStation.get();
            result.add(preStation);
            preLineStation = sections.stream()
                .filter(section -> section.isPreStationInSection(preStation.getStation()))
                .findFirst();
        }
        return result;
    }

    private void checkRemovableSections() {
        if (this.sections.size() < REMOVABLE_SECTION_SIZE) {
            throw new IllegalArgumentException("노선에 제거 가능한 역이 없습니다.");
        }
    }

    private void checkValidStation(boolean includeUpStationInSection, boolean includeDownStationInSection) {
        if (includeUpStationInSection && includeDownStationInSection) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있습니다.");
        }

        if (!includeUpStationInSection && !includeDownStationInSection) {
            throw new IllegalArgumentException("상행역과 하행역 중 노선에 등록된 역이 없습니다.");
        }
    }

    private boolean findByStation(Station station) {
        return this.sections.stream()
            .anyMatch(section -> section.isStationInSection(station));
    }
}
