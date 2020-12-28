package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.EndUpStationNotFoundException;
import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

public class SectionsInLineExplorer {
    private final List<Section> sections;

    public SectionsInLineExplorer(final List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    List<Long> getStationIdsOrderBySection() {
        List<Long> stationIds = new ArrayList<>();

        Section endUpSection = this.findEndUpSection();
        stationIds.add(endUpSection.getUpStationId());
        stationIds.add(endUpSection.getDownStationId());

        Section nextSection = this.findNextSection(endUpSection);
        while (nextSection != null) {
            stationIds.add(nextSection.getDownStationId());
            nextSection = this.findNextSection(nextSection);
        }

        return stationIds;
    }

    boolean isInEndSection(final Section section) {
        Section endUpSection = this.findEndUpSection();
        Section endDownSection = this.findEndDownSection();

        return endUpSection.isSameUpWithThatDown(section) || endDownSection.isSameDownWithThatUp(section);
    }

    Section findEndUpSection() {
        return sections.stream().filter(it -> it.isUpStationBelongsTo(calculateSingleStationIds()))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("상행종점역 구간을 찾을 수 없습니다."));
    }

    Section findEndDownSection() {
        return sections.stream().filter(it -> it.isDownStationBelongsTo(calculateSingleStationIds()))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("하행종점역 구간을 찾을 수 없습니다."));
    }

    Section findNextSection(final Section targetSection) {
        return sections.stream()
                .filter(it -> it.isSameUpWithThatDown(targetSection))
                .findFirst()
                .orElse(null);
    }

    List<Long> getStationIds() {
        return sections.stream()
                .flatMap(it -> it.getStationIds().stream())
                .collect(Collectors.toList());
    }

    List<Long> calculateSingleStationIds() {
        return this.getStationIds().stream()
                .collect(Collectors.groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .filter(it -> it.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    Section findTargetSection(final Section newSection) {
        Section targetSection = findSameWithUpStation(newSection);
        if (targetSection == null) {
            targetSection = findSameWithDownStation(newSection);
        }
        if (targetSection == null) {
            throw new TargetSectionNotFoundException("새로운 구간을 추가할 수 있는 구간이 없습니다.");
        }

        return targetSection;
    }

    Section findSameWithUpStation(final Section section) {
        return sections.stream()
                .filter(it -> it.isSameUpStation(section) && !it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }

    Section findSameWithDownStation(final Section section) {
        return sections.stream()
                .filter(it -> !it.isSameUpStation(section) && it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }

    List<Section> findRelatedSections(final Long targetStationId) {
        return sections.stream()
                .filter(it -> it.isHasThisStation(targetStationId))
                .collect(Collectors.toList());
    }
}
