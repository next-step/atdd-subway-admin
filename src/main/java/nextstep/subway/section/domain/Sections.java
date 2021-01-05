package nextstep.subway.section.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.section.application.AlreadyExistsException;
import nextstep.subway.section.application.ExceedDistanceException;
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

    public static Sections of(List<Section> sections) {
        return new Sections(sections);
    }

    public List<Long> allDistinctStationIds() {
        List<Long> results = new ArrayList<>();
        Sections orderSections = orderedSections();
        orderSections.sections.forEach(it -> addStationIds(results, it));
        return results;
    }

    public void addSection(Section newSection) {
        validate(newSection);

        if(sections.isEmpty()) {
            this.sections.add(new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
            return;
        }

        if (firstStationId().equals(newSection.getDownStationId())) {
            this.sections.add(new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
            return;
        }

        if (lastStationId().equals(newSection.getUpStationId())) {
            this.sections.add(new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
            return;
        }

        this.sections.stream()
                .filter(orgSection -> orgSection.getDownStationId().equals(newSection.getDownStationId()))
                .findFirst()
                .ifPresent(orgSection -> {
                    validateDistance(orgSection, newSection);
                    this.sections.add(new Section(orgSection.getUpStationId(), orgSection.getDownStationId(), orgSection.getDistance() - newSection.getDistance()));
                    this.sections.add(new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
                    this.sections.remove(orgSection);
                });

        this.sections.stream()
                .filter(orgSection -> orgSection.getUpStationId().equals(newSection.getUpStationId()))
                .findFirst()
                .ifPresent(orgSection -> {
                    validateDistance(orgSection, newSection);
                    this.sections.add(new Section(newSection.getUpStationId(), newSection.getDownStationId(), newSection.getDistance()));
                    this.sections.add(new Section(newSection.getDownStationId(), orgSection.getDownStationId(), orgSection.getDistance() - newSection.getDistance()));
                    this.sections.remove(orgSection);
                });
    }

    private void validate(Section newSection) {
        if (sections.isEmpty()) {
            return;
        }
        if (sections.contains(newSection)) {
            throw new AlreadyExistsException(newSection);
        }
        if (noMatchAnyStationId(newSection)) {
            throw new NoMatchStationException(newSection);
        }
    }

    private boolean noMatchAnyStationId(Section newSection) {
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

    private void validateDistance(Section orgSection, Section newSection) {
        if (newSection.longer(orgSection)) {
            throw new ExceedDistanceException(orgSection.getDistance(), newSection.getDistance());
        }
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
                .filter(this::isFirst)
                .findAny()
                .get();
    }

    private Optional<Section> getNextSection(Section section) {
        return sections.stream()
                .filter(it -> it.getUpStationId().equals(section.getDownStationId()))
                .findAny();
    }

    private boolean isFirst(Section section) {
        return sections.stream()
                .noneMatch(it -> section.getUpStationId().equals(it.getDownStationId()));
    }

    private boolean isLast(Section section) {
        return sections.stream()
                .noneMatch(it -> section.getDownStationId().equals(it.getUpStationId()));
    }

    private List<Long> addStationIds(List<Long> results, Section it) {
        for (Long stationId : it.allStationIds()) {
            if (!results.contains(stationId)) results.add(stationId);
        }
        return results;
    }

    private Long firstStationId() {
        return getFirstSection().getUpStationId();
    }

    private Long lastStationId() {
        Section section = sections.stream()
                .filter(this::isLast)
                .findAny()
                .get();
        return section.getDownStationId();
    }
}
