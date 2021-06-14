package nextstep.subway.section.domain;

import nextstep.subway.section.application.SectionDuplicatedException;
import nextstep.subway.section.application.StationNotRegisterException;
import nextstep.subway.station.application.StationNotFoundException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Sections {
    private static final String SECTION_DUPLICATE = "이미 등록된 구간입니다.";
    private static final String NOT_REGISTERED_STATION = "주어진 역을 포함하지 않아 추가할 수 없습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections;

    public Sections() {
        this.sections = new LinkedList<>();
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public void addSection(Section section) {
        if (sections.isEmpty()) {
            this.sections.add(section);
            return;
        }
        validate(section);
        LinkedList<Section> result = new LinkedList(sections);
        Section first = result.getFirst();
        if (isFirstSectionNext(section, first)) {
            addFirstSectionNext(section, result, first);
            return;
        }
        if (isFirstSectionBefore(section, first)) {
            addFirstSectionBefore(section, result, first);
            return;
        }
        Section last = result.getLast();
        if (isLastSectionNext(section, last)) {
            addLastSectionNext(section, result, last);
            return;
        }
        if (isLastSectionBefore(section, last)) {
            addLastSectionBefore(section, result, last);
            return;
        }
        addMiddleSection(section, result);
    }

    private boolean isLastSectionBefore(Section section, Section last) {
        return last.isLastSectionBefore(section);
    }

    private boolean isLastSectionNext(Section section, Section last) {
        return last.isLastSectionNext(section);
    }

    private boolean isFirstSectionBefore(Section section, Section first) {
        return first.isFirstSectionBefore(section);
    }

    private boolean isFirstSectionNext(Section section, Section first) {
        return first.isFirstSectionNext(section);
    }

    private void addMiddleSection(Section section, LinkedList<Section> result) {
        AtomicInteger foundIndex = new AtomicInteger();
        IntStream.range(0, result.size())
                .filter(i -> isUpStationMatch(section, result.get(i)))
                .findFirst()
                .ifPresent(
                        i -> {
                            result.get(i).changeUpStation(section);
                            foundIndex.set(i);
                        }
                );

        IntStream.range(0, result.size())
                .filter(i -> isDownStationMatch(section, result.get(i)))
                .findFirst()
                .ifPresent(
                        i -> {
                            result.get(i).changeDownStation(section);
                            foundIndex.set(i);
                        }
                );

        result.add(foundIndex.get(), section);
        sections = new ArrayList<>(result);
    }

    private boolean isDownStationMatch(Section section, Section foundSection) {
        return foundSection.isSameDownStation(section);
    }

    private boolean isUpStationMatch(Section section, Section foundSection) {
        return foundSection.isSameUpStation(section);
    }

    private void addLastSectionBefore(Section section, LinkedList<Section> result, Section last) {
        last.changeDownStation(section);
        result.addLast(section);
        sections = new ArrayList<>(result);
    }

    private void addLastSectionNext(Section section, LinkedList<Section> result, Section last) {
        last.changeDownStationEdge(section);
        result.addLast(section);
        sections = new ArrayList<>(result);
    }

    private void addFirstSectionBefore(Section section, LinkedList<Section> result, Section first) {
        first.changeUpStationEdge(section);
        result.addFirst(section);
        sections = new ArrayList<>(result);
    }

    private void addFirstSectionNext(Section section, LinkedList<Section> result, Section first) {
        first.changeUpStation(section);
        result.addFirst(section);
        sections = new ArrayList<>(result);
    }

    private void validate(Section section) {
        checkSectionDuplicated(section);
        checkStationExist(section);
    }

    private void checkSectionDuplicated(Section section) {
        if (isSectionDuplicated(section)) {
            throw new SectionDuplicatedException(SECTION_DUPLICATE);
        }
    }

    private void checkStationExist(Section section) {
        if (isSectionStationExisted(section)) {
            throw new StationNotRegisterException(NOT_REGISTERED_STATION);
        }
    }

    private boolean isSectionDuplicated(Section section) {
        return sections.stream()
                .allMatch(section::containsAllStations);
    }

    private boolean isSectionStationExisted(Section section) {
        return sections.stream()
                .allMatch(section::containsNoneStations);
    }

    public List<Station> getStations() {
        return sections.stream().
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

    public void deleteSection(Long stationId) {
        checkCanRemoveSection();
        checkHasStationId(stationId);

        List<Section> foundSections = sections.stream()
                .filter(v -> v.hasStationId(stationId))
                .collect(toList());
        final Section upwardSection = getUpwardSection(stationId, foundSections);
        final Section downwardSection = getDownwardSection(stationId, foundSections);

        LinkedList<Section> orderedSections = new LinkedList<>(this.sections);
        final Section first = orderedSections.getFirst();
        final Section last = orderedSections.getLast();

        if (isFirstSectionDelete(upwardSection, first)) {
            this.sections.remove(first);
            return;
        }
        if (isLastSectionDelete(downwardSection, last)) {
            this.sections.remove(last);
            return;
        }
        downwardSection.changeUpward(upwardSection);
        this.sections.remove(upwardSection);
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

    private boolean isLastSectionDelete(Section downSection, Section last) {
        return last.equals(downSection);
    }

    private boolean isFirstSectionDelete(Section upSection, Section first) {
        return first.equals(upSection);
    }

    private void checkCanRemoveSection() {
        if (sections.size() < 2) {
            throw new IllegalStateException();
        }
    }
}
