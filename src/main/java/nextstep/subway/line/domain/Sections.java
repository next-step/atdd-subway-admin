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

    public boolean addSection(final Section newSection) {
        int originalSize = this.sections.size();

        if (sections.size() == 0) {
            throw new InvalidSectionsActionException("초기화되지 않은 Sections에 Section을 추가할 수 없습니다.");
        }

        TargetSectionSelector targetSectionSelector = new TargetSectionSelector(this.sections);
        Section targetSection = targetSectionSelector.findTargetSection(newSection);

        AddSectionPolicy addSectionPolicy = AddSectionPolicy.find(targetSection, newSection);
        addSectionPolicy.calculateOriginalSection(targetSection, newSection);
        this.sections.add(newSection);

        return (this.sections.size() == originalSize + 1);
    }

    public Section findEndUpSection() {
        List<Long> singleStationIds = calculateSingleStationIds();

        return this.sections.stream().filter(it -> it.isUpStationBelongsTo(singleStationIds))
                .findFirst()
                .orElseThrow(() -> new EndUpStationNotFoundException("상행종점역 구간을 찾을 수 없습니다."));
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

    boolean contains(final Section section) {
        return this.sections.contains(section);
    }
}
