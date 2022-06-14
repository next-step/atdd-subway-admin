package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    public static final String ERROR_EXISTS_SECTION = "이미 존재하는 구간입니다.";
    public static final String ERROR_CAN_NOT_CONNECT_SECTION = "연결되는 구간을 찾을 수 없습니다.";
    public static final String ERROR_NOT_FOUND_FIRST_STATION = "첫번째 역을 찾을 수 없습니다.";
    public static final String ERROR_REMOVE_ONLY_ONE_SECTION = "단일 구간만이 존재할 경우 구간 제거가 불가합니다.";
    public static final String ERROR_STATION_NOT_EXISTS_ON_LINE = "노선 상에 존재하지 않는 역입니다.";

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "line_id")
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public List<Station> getSortedStations() {
        List<Station> stations = new ArrayList<>();

        Station firstStation = getFirstStation();
        stations.add(firstStation);

        Station nextStation = getNextStation(firstStation);
        while (nextStation != null) {
            stations.add(nextStation);
            nextStation = getNextStation(nextStation);
        }

        return stations;
    }

    private Station getFirstStation() {
        return sections.stream().map(Section::getUpStation)
                .filter(upStation -> !isExistsDownStationOfSections(upStation)).findFirst()
                .orElseThrow(() -> new NoSuchElementException(ERROR_NOT_FOUND_FIRST_STATION));
    }

    private boolean isExistsDownStationOfSections(Station station) {
        return sections.stream().anyMatch(section -> section.equalDownStation(station));
    }

    private Station getNextStation(Station currentStation) {
        return sections.stream().filter(section -> section.equalUpStation(currentStation))
                .map(Section::getDownStation).findFirst().orElse(null);
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }

        validateForAddSection(section);
        addSectionInternal(section);
    }

    private void addSectionInternal(Section section) {
        Section matchedSection = findMatchedSection(section);
        if (matchedSection != null) {
            addSectionWithUpdateMatchedSection(matchedSection, section);
            return;
        }

        sections.add(section);
    }

    private Section findMatchedSection(Section targetSection) {
        return sections.stream().filter(section -> section.match(targetSection)).findFirst().orElse(null);
    }


    private void addSectionWithUpdateMatchedSection(Section matchedSection, Section newSection) {
        matchedSection.updateForDivide(newSection);
        sections.add(newSection);
    }

    private void validateForAddSection(Section section) {
        validateExists(section);
        validateConnected(section);
    }

    private void validateExists(Section section) {
        if (checkExistsStation(section.getUpStation()) && checkExistsStation(section.getDownStation())) {
            throw new IllegalArgumentException(ERROR_EXISTS_SECTION);
        }
    }

    private void validateConnected(Section section) {
        if (!checkExistsStation(section.getUpStation()) && !checkExistsStation(section.getDownStation())) {
            throw new IllegalArgumentException(ERROR_CAN_NOT_CONNECT_SECTION);
        }
    }

    private boolean checkExistsStation(Station station) {
        return sections.stream().anyMatch(section -> section.hasStation(station));
    }

    public void removeSection(Station station) {
        validateForRemoveSection(station);
        removeSectionInternal(station);
    }

    private void removeSectionInternal(Station station) {
        Section upsideSection = getUpsideSection(station);
        Section downsideSection = getDownsideSection(station);

        if(upsideSection == null && downsideSection != null) {
            sections.remove(downsideSection);
            return;
        }

        if(upsideSection != null && downsideSection == null) {
            sections.remove(upsideSection);
            return;
        }

        removeSectionWithCombineSection(upsideSection, downsideSection);
    }

    private void removeSectionWithCombineSection(Section upsideSection, Section downsideSection) {
        upsideSection.updateForCombine(downsideSection);
        sections.remove(downsideSection);
    }

    private Section getUpsideSection(Station station) {
        return sections.stream().filter(section -> section.equalDownStation(station)).findFirst().orElse(null);
    }

    private Section getDownsideSection(Station station) {
        return sections.stream().filter(section -> section.equalUpStation(station)).findFirst().orElse(null);
    }

    private void validateForRemoveSection(Station station) {
        validateSectionsSize();
        validateExistsStation(station);

    }

    private void validateSectionsSize() {
        if (sections.size() <= 1) {
            throw new NoSuchElementException(ERROR_REMOVE_ONLY_ONE_SECTION);
        }
    }

    private void validateExistsStation(Station station) {
        if (!checkExistsStation(station)) {
            throw new IllegalArgumentException(ERROR_STATION_NOT_EXISTS_ON_LINE);
        }
    }
}
