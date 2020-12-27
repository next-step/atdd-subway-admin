package nextstep.subway.line.domain.sections;

import nextstep.subway.line.domain.exceptions.*;
import nextstep.subway.line.domain.exceptions.InvalidSectionsActionException;
import nextstep.subway.line.domain.exceptions.TargetSectionNotFoundException;
import nextstep.subway.line.domain.exceptions.TooLongSectionException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @Transient
    private static final SectionsInLineExplorer sectionExplorer = new SectionsInLineExplorer();

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
        Section targetSection = sectionExplorer.findTargetSection(this.sections, newSection);
        validateWhenAddNotEndSection(newSection, targetSection);

        OriginalSectionCalculator originalSectionCalculator = OriginalSectionCalculator.find(targetSection, newSection);
        Section updatedOriginalSection = originalSectionCalculator.calculate(targetSection, newSection);

        this.sections.remove(targetSection);
        this.sections.add(updatedOriginalSection);
        this.sections.add(newSection);

        return (sections.size() == originalSize + 1);
    }

    public boolean isInEndSection(final Section targetSection) {
        return sectionExplorer.isInEndSection(sections, targetSection);
    }

    public List<Long> getStationIdsOrderBySection() {
        return sectionExplorer.getStationIdsOrderBySection(sections);
    }

    public boolean isThisEndStation(Long stationId) {
        return sectionExplorer.findEndUpSection(this.sections).getUpStationId().equals(stationId) ||
                sectionExplorer.findEndDownSection(this.sections).getDownStationId().equals(stationId);
    }

    public boolean mergeSectionsByStation(final Long stationId) {
        int validTargetSize = 2;
        int originalSize = this.sections.size();

        List<Section> relatedSections = sectionExplorer.findRelatedSections(this.sections, stationId);
        if (relatedSections.size() != validTargetSize) {
            throw new MergeSectionFailException("병합할 수 없는 중간역입니다.");
        }
        relatedSections.forEach(this::removeSection);

        Section mergedSection = relatedSections.get(0).merge(relatedSections.get(1));
        this.sections.add(mergedSection);

        return (this.sections.size() == originalSize - 1);
    }

    public boolean deleteEndStation(final Long stationId) {
        int validTargetSize = 1;
        int originalSize = this.sections.size();

        List<Section> relatedSections = sectionExplorer.findRelatedSections(this.sections, stationId);
        if(relatedSections.size() != validTargetSize) {
            throw new InvalidStationDeleteTryException("종점이 아닌 역을 종점 삭제 기능으로 제거할 수 없습니다.");
        }
        this.sections.remove(relatedSections.get(0));

        return (this.sections.size() == originalSize - 1);
    }

    private void removeSection(final Section section) {
        this.sections.remove(section);
    }

    private void validateWhenAddEndSection(final Section newSection) {
        validateIsInit();
        if (!sectionExplorer.isInEndSection(sections, newSection)) {
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

        return sectionExplorer.getStationIds(this.sections).stream()
                .distinct()
                .collect(Collectors.toList())
                .containsAll(stationIds);
    }
}
