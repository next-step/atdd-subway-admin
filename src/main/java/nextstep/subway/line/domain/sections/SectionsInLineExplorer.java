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
    List<Long> getStationIdsOrderBySection(final List<Section> sectionsList) {
        List<Section> sections = new ArrayList<>(sectionsList);

        List<Long> stationIds = new ArrayList<>();

        Section endUpSection = this.findEndUpSection(sections);
        stationIds.add(endUpSection.getUpStationId());
        stationIds.add(endUpSection.getDownStationId());

        Section nextSection = this.findNextSection(sections, endUpSection);
        while (nextSection != null) {
            stationIds.add(nextSection.getDownStationId());
            nextSection = this.findNextSection(sections, nextSection);
        }

        return stationIds;
    }

    boolean isInEndSection(final List<Section> sectionList, final Section section) {
        List<Section> sections = new ArrayList<>(sectionList);

        Section endUpSection = this.findEndUpSection(sections);
        Section endDownSection = this.findEndDownSection(sections);

        return endUpSection.isSameUpWithThatDown(section) || endDownSection.isSameDownWithThatUp(section);
    }

    Section findEndUpSection(final List<Section> sectionList) {
        List<Section> sections = new ArrayList<>(sectionList);

        return sections.stream().filter(it -> it.isUpStationBelongsTo(calculateSingleStationIds(sections)))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("상행종점역 구간을 찾을 수 없습니다."));
    }

    Section findEndDownSection(final List<Section> sectionList) {
        List<Section> sections = new ArrayList<>(sectionList);

        return sections.stream().filter(it -> it.isDownStationBelongsTo(calculateSingleStationIds(sections)))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("하행종점역 구간을 찾을 수 없습니다."));
    }

    Section findNextSection(final List<Section> sectionList, final Section targetSection) {
        List<Section> sections = new ArrayList<>(sectionList);

        return sections.stream()
                .filter(it -> it.isSameUpWithThatDown(targetSection))
                .findFirst()
                .orElse(null);
    }

    List<Long> getStationIds(final List<Section> sectionList) {
        List<Section> sections = new ArrayList<>(sectionList);

        return sections.stream()
                .flatMap(it -> it.getStationIds().stream())
                .collect(Collectors.toList());
    }

    List<Long> calculateSingleStationIds(final List<Section> sectionList) {
        List<Section> sections = new ArrayList<>(sectionList);

        return this.getStationIds(sections).stream()
                .collect(Collectors.groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .filter(it -> it.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    Section findTargetSection(final List<Section> sectionList, final Section newSection) {
        List<Section> sections = new ArrayList<>(sectionList);

        Section targetSection = findSameWithUpStation(sections, newSection);
        if (targetSection == null) {
            targetSection = findSameWithDownStation(sections, newSection);
        }
        if (targetSection == null) {
            throw new TargetSectionNotFoundException("새로운 구간을 추가할 수 있는 구간이 없습니다.");
        }

        return targetSection;
    }

    Section findSameWithUpStation(final List<Section> sectionList, final Section section) {
        List<Section> sections = new ArrayList<>(sectionList);

        return sections.stream()
                .filter(it -> it.isSameUpStation(section) && !it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }

    Section findSameWithDownStation(final List<Section> sectionList, final Section section) {
        List<Section> sections = new ArrayList<>(sectionList);

        return sections.stream()
                .filter(it -> !it.isSameUpStation(section) && it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }

    List<Section> findRelatedSections(final List<Section> sectionList, final Long targetStationId) {
        List<Section> sections = new ArrayList<>(sectionList);

        return sections.stream()
                .filter(it -> it.isHasThisStation(targetStationId))
                .collect(Collectors.toList());
    }
}
