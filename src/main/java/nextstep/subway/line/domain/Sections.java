package nextstep.subway.line.domain;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import nextstep.subway.line.domain.exceptions.EndUpStationNotFoundException;

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

    public int size() {
        return sections.size();
    }

    public void initFirstSection(final Section section) {
        this.sections.add(section);
    }

    public List<Long> getStationIdsWithoutDup() {
        return this.getStationIds().stream()
                .distinct()
                .collect(Collectors.toList());
    }

    // TODO: 리팩토링 과정에서 임시로 사용할 메서드
    void addSectionRaw(final Section section) {
        if (sections.size() == 0) {
            throw new InvalidSectionsActionException("초기화되지 않은 Sections에 Section을 추가할 수 없습니다.");
        }

        this.sections.add(section);
    }

    // TODO: 기능 구현 다 한 뒤에 나눠야 함. 너무 큼
    // TODO2: 실질적으로는 동작을 조각내고 서비스 레이어에서 조합하는 방향으로 가야될 수도 있을 것 같음 (서비스에서 너무 플로우가 안보임)
    public boolean addSection(final Section newSection) {
        int originalSize = this.sections.size();

        if (sections.size() == 0) {
            throw new InvalidSectionsActionException("초기화되지 않은 Sections에 Section을 추가할 수 없습니다.");
        }

        // 상행 종점역이나 하행 종점역을 추가하는 경우 로직 진행
        if (isEndSectionAddCase(newSection)) {
            this.sections.add(newSection);
            return (this.sections.size() == originalSize + 1);
        }

        // 상행역도 하행역도 아닌 경우 구간을 추가하는 로직 진행
        TargetSectionSelector targetSectionSelector = new TargetSectionSelector(this.sections);
        Section targetSection = targetSectionSelector.findTargetSection(newSection);

        OriginalSectionCalculator originalSectionCalculator = OriginalSectionCalculator.find(targetSection, newSection);
        originalSectionCalculator.calculate(targetSection, newSection);
        this.sections.add(newSection);

        return (this.sections.size() == originalSize + 1);
    }

    // TODO: 최종적으로 Section 추가시 사용하게 될 추가 메서드
    public boolean addSectionByPolicy(final AddSectionPolicy addSectionPolicy, final Section newSection) {
        return addSectionPolicy.addSection(newSection);
    }

    boolean contains(final Section section) {
        return this.sections.contains(section);
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

    boolean isEndSectionAddCase(final Section newSection) {
        Section endUpSection = findEndUpSection();
        Section endDownSection = findEndDownSection();

        return endUpSection.isSameUpWithThatDown(newSection) || endDownSection.isSameDownWithThatUp(newSection);
    }

    private List<Long> getStationIds() {
        return sections.stream()
                .flatMap(it -> it.getStationIds().stream())
                .collect(Collectors.toList());
    }

    private List<Long> calculateSingleStationIds() {
        return this.getStationIds().stream()
                .collect(Collectors.groupingBy(Function.identity(), counting()))
                .entrySet().stream()
                .filter(it -> it.getValue() == 1L)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
