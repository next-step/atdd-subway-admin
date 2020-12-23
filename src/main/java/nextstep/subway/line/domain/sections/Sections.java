package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import nextstep.subway.line.domain.exceptions.EndUpStationNotFoundException;
import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;

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

    public List<Long> getStationIdsWithoutDup() {
        return this.getStationIds().stream()
                .distinct()
                .collect(Collectors.toList());
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

    public Section findEndUpSection() {
        List<Long> singleStationIds = calculateSingleStationIds();

        return this.sections.stream().filter(it -> it.isUpStationBelongsTo(singleStationIds))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("상행종점역 구간을 찾을 수 없습니다."));
    }

    public Section findEndDownSection() {
        List<Long> singleStationIds = calculateSingleStationIds();

        return this.sections.stream().filter(it -> it.isDownStationBelongsTo(singleStationIds))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("하행종점역 구간을 찾을 수 없습니다."));
    }

    // 패키지 외부로 노출되면 도메인을 심각하게 손상할 수 있는 메서드
    void addSection(final Section section) {
        if (sections.size() == 0) {
            throw new InvalidSectionsActionException("초기화되지 않은 Sections에 Section을 추가할 수 없습니다.");
        }

        this.sections.add(section);
    }

    boolean isAllStationsIn(final Section newSection) {
        List<Long> stationIds = newSection.getStationIds();

        return this.getStationIdsWithoutDup().containsAll(stationIds);
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
