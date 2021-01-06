package nextstep.subway.section.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.application.AlreadyExistsException;
import nextstep.subway.section.application.CannotRemoveException;
import nextstep.subway.section.application.NoMatchStationException;

import javax.persistence.*;
import java.util.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class Sections {
    public static final int ZERO = 0;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "line_id", foreignKey = @ForeignKey(name = "fk_section_to_line"))
    private List<Section> sections = new ArrayList<>();

    public Sections(Section section) {
        this.sections = Arrays.asList(section);
    }

    public List<Long> allDistinctStationIds() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }

        Sections orderSections = orderedSections();
        return orderSections.getStationIds();
    }

    public void addSection(Section newSection) {
        validate(newSection);

        if (sections.isEmpty() || isFirstOrLastSection(newSection)) {
            this.sections.add(newSection);
            return;
        }
        insertWhenSameDownStation(newSection);
        insertWhenSameUpStation(newSection);
    }

    public void removeSectionByStationId(Long stationId) {
        if(sections.size() == 1) {
            throw new CannotRemoveException("최종 구간의 역은 삭제할 수 없습니다.");
        }

        if(firstStationId().equals(stationId)) {
            sections.remove(getFirstSection());
            return;
        }

        if(lastStationId().equals(stationId)) {
            sections.remove(getLastSection());
            return;
        }

        Section preSection = getPreSectionByStationId(stationId);
        Section nextSection = getNextSectionByStationId(stationId);
        Section newSection = Section.builder()
                .upStationId(preSection.getUpStationId())
                .downStationId(nextSection.getDownStationId())
                .distance(preSection.getDistance() + nextSection.getDistance())
                .build();

        sections.add(newSection);
        sections.removeAll(Arrays.asList(preSection, nextSection));
    }

    private Optional<Section> getPreSection(Section section) {
        return sections.stream()
                .filter(it -> it.getDownStationId().equals(section.getUpStationId()))
                .findFirst();
    }

    private Section getNextSectionByStationId(Long stationId) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(stationId))
                .findFirst()
                .get();
    }

    private Section getPreSectionByStationId(Long stationId) {
        return sections.stream()
                .filter(it -> it.getDownStationId().equals(stationId))
                .findFirst()
                .get();
    }

    private void insertWhenSameUpStation(Section newSection) {
        this.sections.stream()
                .filter(newSection::sameUpStation)
                .findFirst()
                .ifPresent(orgSection -> {
                    this.sections.addAll(orgSection.splitWhenSameUpStation(newSection));
                    this.sections.remove(orgSection);
                });
    }

    private void insertWhenSameDownStation(Section newSection) {
        this.sections.stream()
                .filter(newSection::sameDownStation)
                .findFirst()
                .ifPresent(orgSection -> {
                    this.sections.addAll(orgSection.splitWhenSameDownStation(newSection));
                    this.sections.remove(orgSection);
                });
    }

    private boolean isFirstOrLastSection(Section section) {
        return firstStationId().equals(section.getDownStationId())
                || lastStationId().equals(section.getUpStationId());
    }

    private void validate(Section newSection) {
        if (sections.isEmpty()) {
            return;
        }
        if (newSection.getUpStationId() == null) {
            throw new NoStationIdException("상행역을 입력해 주세요.");
        }

        if (newSection.getDownStationId() == null) {
            throw new NoStationIdException("하행역을 입력해 주세요.");
        }

        if (sections.contains(newSection)) {
            throw new AlreadyExistsException(newSection);
        }
        if (noMatchExistsStation(newSection)) {
            throw new NoMatchStationException(newSection);
        }
    }

    private boolean noMatchExistsStation(Section newSection) {
        Set<Long> ids = allStationId();
        return !(ids.contains(newSection.getDownStationId())
                || ids.contains(newSection.getUpStationId()));
    }

    private Set<Long> allStationId() {
        Set<Long> ids = new HashSet<>();
        for (Section section : sections) {
            ids.add(section.getUpStationId());
            ids.add(section.getDownStationId());
        }
        return ids;
    }

    private Sections orderedSections() {
        List<Section> orderedSections = new ArrayList<>();

        Section firstSection = getFirstSection();
        orderedSections.add(firstSection);
        Optional<Section> nextSection = getNextSection(firstSection);
        while (nextSection.isPresent()) {
            orderedSections.add(nextSection.get());
            nextSection = getNextSection(nextSection.get());
        }
        return new Sections(orderedSections);
    }

    private Section getFirstSection() {
        return sections.stream()
                .filter(this::isFirstSection)
                .findAny()
                .get();
    }

    private Section getLastSection() {
        return sections.stream()
                .filter(this::isLastSection)
                .findAny()
                .get();
    }

    private Optional<Section> getNextSection(Section section) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(section.getDownStationId()))
                .findAny();
    }

    private boolean isFirstSection(Section section) {
        return sections.stream()
                .noneMatch(it -> section.getUpStationId().equals(it.getDownStationId()));
    }

    private boolean isLastSection(Section section) {
        return sections.stream()
                .noneMatch(it -> section.getDownStationId().equals(it.getUpStationId()));
    }

    private Long firstStationId() {
        return getFirstSection().getUpStationId();
    }

    private Long lastStationId() {
        Section section = sections.stream()
                .filter(this::isLastSection)
                .findAny()
                .get();
        return section.getDownStationId();
    }

    private List<Long> getStationIds() {
        List<Long> stationIds = new ArrayList<>();

        for (Section section : this.sections) {
            stationIds.add(section.getUpStationId());
        }
        stationIds.add(lastStationId());

        return stationIds;
    }
}
