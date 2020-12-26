package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import nextstep.subway.line.domain.exceptions.EndUpStationNotFoundException;
import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
import nextstep.subway.line.domain.exceptions.TooLongSectionException;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.counting;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    Sections() {
        this(new ArrayList<>());
    }

    Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public static Sections of() {
        return new Sections();
    }

    public int size() {
        return sections.size();
    }

    public void initFirstSection(final Section section) {
        this.sections.add(section);
    }

    public Section findTargetSection(final Section newSection) {
        Section targetSection = findSameWithUpStation(newSection);
        if (targetSection == null) {
            targetSection = findSameWithDownStation(newSection);
        }
        if (targetSection == null) {
            throw new TargetSectionNotFoundException("새로운 구간을 추가할 수 있는 구간이 없습니다.");
        }

        return targetSection;
    }

    public boolean containsAll(final List<Section> targetSections) {
        return targetSections.stream().allMatch(this::contains);
    }

    public boolean contains(final Section section) {
        return this.sections.contains(section);
    }

    public boolean addEndSection(final Section newSection) {
        int originalSize = sections.size();

        validateWhenAddEndSection(newSection);
        this.sections.add(newSection);

        return (sections.size() == originalSize + 1);
    }

    public boolean addNotEndSection(final Section newSection) {
        int originalSize = sections.size();
        Section targetSection = this.findTargetSection(newSection);
        validateWhenAddNotEndSection(newSection, targetSection);

        OriginalSectionCalculator originalSectionCalculator = OriginalSectionCalculator.find(targetSection, newSection);
        Section updatedOriginalSection = originalSectionCalculator.calculate(targetSection, newSection);

        this.sections.remove(targetSection);
        this.sections.add(updatedOriginalSection);
        this.sections.add(newSection);

        return (sections.size() == originalSize + 1);
    }

    public boolean isInEndSection(final Section section) {
        Section endUpSection = this.findEndUpSection();
        Section endDownSection = this.findEndDownSection();
        return endUpSection.isSameUpWithThatDown(section) || endDownSection.isSameDownWithThatUp(section);
    }

    Section findEndUpSection() {
        List<Long> singleStationIds = calculateSingleStationIds();

        return this.sections.stream().filter(it -> it.isUpStationBelongsTo(singleStationIds))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("상행종점역 구간을 찾을 수 없습니다."));
    }

    Section findEndDownSection() {
        List<Long> singleStationIds = calculateSingleStationIds();

        return this.sections.stream().filter(it -> it.isDownStationBelongsTo(singleStationIds))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("하행종점역 구간을 찾을 수 없습니다."));
    }

    Section findNextSection(final Section section) {
        return this.sections.stream()
                .filter(it -> it.isSameUpWithThatDown(section))
                .findFirst()
                .orElse(null);
    }

    private List<Long> calculateSingleStationIds() {
        return this.getStationIds().stream()
                .collect(Collectors.groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .filter(it -> it.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private void validateWhenAddEndSection(final Section newSection) {
        validateIsInit();
        if (!isInEndSection(newSection)) {
            throw new InvalidSectionsActionException("종점이 아닌 구간으로 종점 구간 추가를 수행할 수 없습니다.");
        }
    }

    private void validateWhenAddNotEndSection(final Section newSection, final Section targetSection) {
        validateIsInit();
        if (targetSection == null) {
            throw new TargetSectionNotFoundException("기존 구간 중 연결할 수 있는 구간이 없습니다.");
        }
        if (this.isAllStationsIn(newSection)) {
            throw new TargetSectionNotFoundException("이미 모든 역이 노선에 존재합니다.");
        }
        if (newSection.isHasBiggerDistance(targetSection)) {
            throw new TooLongSectionException("추가할 구간의 길이가 너무 깁니다.");
        }
    }

    private void validateIsInit() {
        if (sections.size() == 0) {
            throw new InvalidSectionsActionException("초기화되지 않은 Sections에 Section을 추가할 수 없습니다.");
        }
    }

    private boolean isAllStationsIn(final Section newSection) {
        List<Long> stationIds = newSection.getStationIds();

        return this.getStationIds().stream()
                .distinct()
                .collect(Collectors.toList())
                .containsAll(stationIds);
    }

    private List<Long> getStationIds() {
        return sections.stream()
                .flatMap(it -> it.getStationIds().stream())
                .collect(Collectors.toList());
    }

    private Section findSameWithUpStation(final Section section) {
        return this.sections.stream()
                .filter(it -> it.isSameUpStation(section) && !it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }

    private Section findSameWithDownStation(final Section section) {
        return this.sections.stream()
                .filter(it -> !it.isSameUpStation(section) && it.isSameDownStation(section))
                .findFirst()
                .orElse(null);
    }
}
