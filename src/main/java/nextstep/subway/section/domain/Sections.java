package nextstep.subway.section.domain;

import nextstep.subway.section.application.SectionDuplicatedException;
import nextstep.subway.section.application.StationNotRegisterException;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Stack;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {
    private static final String SECTION_DUPLICATE = "이미 등록된 구간입니다.";
    private static final String NOT_REGISTERED_STATION = "주어진 역을 포함하지 않아 추가할 수 없습니다.";
    private static final int MINIMUM_REMOVE_LIMIT = 2;

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        this.sections = new LinkedList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        checkSectionDuplicate(section);
        checkStationExists(section);

        updateDownStation(section);
        updateUpStation(section);
        sections.add(section);
    }

    public void deleteSection(Long stationId) {
        checkCanRemoveSection();
        checkHasStationId(stationId);

        List<Section> foundSections = getTargetSections(stationId);
        final Section upwardSection = getUpwardSection(stationId, foundSections);
        final Section downwardSection = getDownwardSection(stationId, foundSections);

        if (deleteFirstSection(upwardSection, downwardSection)) return;
        if (deleteLastSection(upwardSection, downwardSection)) return;

        downwardSection.changeUpward(upwardSection);
        this.sections.remove(upwardSection);
    }

    public List<Station> getStations() {
        Optional<Section> firstSection = findFirstSection();
        if (!firstSection.isPresent()) {
            return new LinkedList<>();
        }
        LinkedList<Section> result = appendSections(firstSection.get());
        return collectStations(result);
    }

    private void checkSectionDuplicate(Section section) {
        if (sections.stream().anyMatch(section::containsAllStations)) {
            throw new SectionDuplicatedException(SECTION_DUPLICATE);
        }
    }

    private void checkStationExists(Section section) {
        if (sections.size() > 0) {
            boolean upStationExist = sections.stream().anyMatch(section::hasUpStation);
            boolean downStationExist = sections.stream().anyMatch(section::hasDownStation);

            if (!upStationExist && !downStationExist) {
                throw new StationNotRegisterException(NOT_REGISTERED_STATION);
            }
        }
    }

    private void updateDownStation(Section section) {
        sections.stream()
                .filter(section::isSameDownStation)
                .findFirst()
                .ifPresent(v -> {
                    v.updateDownStation(section);
                    v.updateMinusDistance(section);
                });
    }

    private void updateUpStation(Section section) {
        sections.stream()
                .filter(section::isSameUpStation)
                .findFirst()
                .ifPresent(v -> {
                    v.updateUpStation(section);
                    v.updateMinusDistance(section);
                });
    }

    public Optional<Section> findFirstSection() {
        Stack<Section> stack = new Stack<>();
        stack.addAll(sections);
        Section firstSection = null;

        while (!stack.isEmpty()) {
            final Section section = stack.pop();
            Optional<Section> upStation = sections.stream()
                    .filter(v -> v.isDownToUpConnected(section))
                    .findFirst();
            firstSection = getFirstSection(firstSection, section, upStation);
        }
        return Optional.ofNullable(firstSection);
    }

    private Section getFirstSection(Section firstSection, Section section, Optional<Section> upStation) {
        if (!upStation.isPresent()) {
            firstSection = section;
        }
        return firstSection;
    }

    private LinkedList<Section> appendSections(Section first) {
        LinkedList<Section> result = new LinkedList<>(Collections.singletonList(first));
        Queue<Section> queue = new LinkedList<>(sections);
        queue.remove(first);

        while (!queue.isEmpty()) {
            sections.stream().
                    filter(v -> v.isUpToDownConnected(result.getLast()))
                    .findFirst()
                    .ifPresent(v -> {
                        result.add(v);
                        queue.poll();
                    });
        }
        return result;
    }

    private List<Station> collectStations(LinkedList<Section> result) {
        return result.stream().
                flatMap(Section::getProcessStations)
                .distinct()
                .collect(toList());
    }

    private void checkHasStationId(Long stationId) {
        sections.stream()
                .filter(v -> v.hasStationId(stationId))
                .findFirst()
                .orElseThrow(StationNotFoundException::new);
    }

    private boolean deleteLastSection(Section upwardSection, Section downwardSection) {
        if (Objects.isNull(downwardSection)) {
            this.sections.remove(upwardSection);
            return true;
        }
        return false;
    }

    private boolean deleteFirstSection(Section upwardSection, Section downwardSection) {
        if (Objects.isNull(upwardSection)) {
            this.sections.remove(downwardSection);
            return true;
        }
        return false;
    }

    private List<Section> getTargetSections(Long stationId) {
        return sections.stream()
                .filter(v -> v.hasStationId(stationId))
                .collect(toList());
    }

    private Section getUpwardSection(Long stationId, List<Section> foundSections) {
        return foundSections.stream()
                .filter(v -> v.isSameUpStation(stationId))
                .findFirst()
                .orElse(null);
    }

    private Section getDownwardSection(Long stationId, List<Section> foundSections) {
        return foundSections.stream()
                .filter(v -> v.isSameDownStation(stationId))
                .findFirst()
                .orElse(null);
    }

    private void checkCanRemoveSection() {
        if (sections.size() < MINIMUM_REMOVE_LIMIT) {
            throw new IllegalStateException();
        }
    }
}
