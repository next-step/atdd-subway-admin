package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {
    private static final int MINIMUM_DELETE_SECTION_SIZE = 1;
    private static final int MINIMUM_STATION_SIZE = 1;
    private static final String NO_DATA_EXCEPTION_MESSAGE = "조건에 부합하는 데이터가 없습니다.";

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public List<Section> getSections() {
        return sections.stream()
                .sorted((s1, s2) -> s1.getUpStation().getName().equals(s2.getDownStation().getName()) ? 0 : -1)
                .collect(Collectors.toList());
    }

    public void addSection(Section section) {
        if (!sections.isEmpty()) {
            validAddSectionCheck(section);
            updateSection(section);
        }
        sections.add(section);
    }

    public void deleteSection(Station deleteStation) {
        validDeleteSectionCheck(deleteStation);
        deleteStation(deleteStation);
    }

    private void validAddSectionCheck(Section section) {
        if (!isContainAnyStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역 둘 중 하나도 포함되어 있지 않으면 추가할 수 없음");
        }

        if (isContainAllStation(section)) {
            throw new IllegalArgumentException("상행역과 하행역이 이미 노선에 모두 등록되어 있으면 추가할 수 없음");
        }
    }

    private void validDeleteSectionCheck(Station deleteStation) {
        if (!isContainStation(deleteStation)) {
            throw new IllegalArgumentException("해당 노선에 존재하지 않는 역");
        }

        if (isLastSection()) {
            throw new IllegalArgumentException("마지막 구간은 삭제할 수 없음");
        }
    }

    private boolean isContainAnyStation(Section newSection) {
        return sections.stream()
                .anyMatch(currentSection -> currentSection.getLineStations().contains(newSection.getUpStation())
                        || currentSection.getLineStations().contains(newSection.getDownStation()));
    }

    private boolean isContainAllStation(Section newSection) {
        return sections.stream()
                .anyMatch(currentSection -> currentSection.getLineStations().contains(newSection.getUpStation())
                        && currentSection.getLineStations().contains(newSection.getDownStation()));
    }

    private void updateSection(Section section) {
        Section targetSection = findTargetSection(section);
        targetSection.changeStationInfo(section);
    }

    private Section findTargetSection(Section newSection) {
        return sections.stream()
                .filter(currentSection -> currentSection.getLineStations().contains(newSection.getUpStation())
                        || currentSection.getLineStations().contains(newSection.getDownStation()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(NO_DATA_EXCEPTION_MESSAGE));
    }

    private boolean isContainStation(Station deleteStation) {
        return sections.stream()
                .flatMap(section -> section.getLineStations().stream())
                .anyMatch(station -> station.equals(deleteStation));
    }

    private boolean isLastSection() {
        return sections.size() <= MINIMUM_DELETE_SECTION_SIZE;
    }

    private void deleteStation(Station deleteStation) {
        if (isStartOrEndStation(deleteStation)) {
            sections.remove(findDeleteTargetSection(deleteStation));
            return;
        }

        Section beforeSection = findBeforeDeleteTargetSection(deleteStation);
        Section afterSection = findAfterDeleteTargetSection(deleteStation);

        beforeSection.combineStationInfo(afterSection);
        sections.remove(afterSection);
    }

    private boolean isStartOrEndStation(Station station) {
        return sections.stream()
                .flatMap(section -> section.getLineStations().stream())
                .filter(currentStation -> currentStation.equals(station))
                .count() == MINIMUM_STATION_SIZE;
    }

    private Section findDeleteTargetSection(Station station) {
        return sections.stream()
                .filter(currentSection -> currentSection.getUpStation().equals(station)
                        || currentSection.getDownStation().equals(station))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(NO_DATA_EXCEPTION_MESSAGE));
    }

    private Section findBeforeDeleteTargetSection(Station station) {
        return sections.stream()
                .filter(currentSection -> currentSection.getDownStation().equals(station))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(NO_DATA_EXCEPTION_MESSAGE));
    }

    private Section findAfterDeleteTargetSection(Station station) {
        return sections.stream()
                .filter(currentSection -> currentSection.getUpStation().equals(station))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(NO_DATA_EXCEPTION_MESSAGE));
    }
}
