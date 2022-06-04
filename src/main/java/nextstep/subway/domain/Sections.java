package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int MINIMUM_DELETE_SECTION_SIZE = 1;
    private static final String NO_STATION_IN_LINE_EXCEPTION_MESSAGE = "해당 노선에 존재하지 않는 역";

    @OneToMany(mappedBy = "line", orphanRemoval = true, cascade = CascadeType.PERSIST)
    private final List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        List<Section> sectionList = new ArrayList<>();
        sectionList.add(findStartSection());

        while (sectionList.size() != sections.size()) {
            sectionList.add(findNextSection(sectionList.get(sectionList.size() - 1).getDownStation()));
        }

        return sectionList;
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            updateSection(section);
        }
        sections.add(section);
    }

    public void deleteSection(Station deleteStation) {
        if (isLastSection()) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없음");
        }

        if (isStartStation(deleteStation) || isEndStation(deleteStation)) {
            sections.remove(findDeleteTargetSection(deleteStation));
            return;
        }

        Section beforeSection = findBeforeDeleteTargetSection(deleteStation);
        Section afterSection = findAfterDeleteTargetSection(deleteStation);

        sections.add(beforeSection.combineSections(afterSection));
        sections.remove(beforeSection);
        sections.remove(afterSection);
    }

    private Section findStartSection() {
        return sections.stream()
                .filter(section -> isStartStation(section.getUpStation()))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private Section findNextSection(Station lastStation) {
        return sections.stream()
                .filter(section -> section.hasUpStation(lastStation))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    private void updateSection(Section section) {
        if (isContainAllStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 추가할 수 없음");
        }

        Section targetSection = findTargetSection(section);
        targetSection.changeStationInfo(section);
    }

    private boolean isContainAllStation(Section newSection) {
         return sections.stream()
                .anyMatch(currentSection -> currentSection.hasAllStations(newSection));
    }

    private Section findTargetSection(Section newSection) {
        return sections.stream()
                .filter(currentSection -> currentSection.hasAnyStations(newSection))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음"));
    }

    private boolean isLastSection() {
        return sections.size() <= MINIMUM_DELETE_SECTION_SIZE;
    }

    private boolean isStartStation(Station station) {
        return sections.stream()
                .noneMatch(currentStation -> currentStation.hasDownStation(station));
    }

    private boolean isEndStation(Station station) {
        return sections.stream()
                .noneMatch(currentStation -> currentStation.hasUpStation(station));
    }

    private Section findDeleteTargetSection(Station station) {
        return sections.stream()
                .filter(currentSection -> currentSection.hasStations(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_STATION_IN_LINE_EXCEPTION_MESSAGE));
    }

    private Section findBeforeDeleteTargetSection(Station station) {
        return sections.stream()
                .filter(currentSection -> currentSection.hasDownStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_STATION_IN_LINE_EXCEPTION_MESSAGE));
    }

    private Section findAfterDeleteTargetSection(Station station) {
        return sections.stream()
                .filter(currentSection -> currentSection.hasUpStation(station))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(NO_STATION_IN_LINE_EXCEPTION_MESSAGE));
    }
}
