package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    private static final int SECTIONS_START_AT = 0;
    private static final int SECTIONS_UNIT = 1;
    private static final boolean SECTION_ADDED = true;
    private static final boolean SECTION_NOT_ADDED = false;

    @OneToMany(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        mappedBy = "line",
        orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        sections.forEach(this::add);
    }

    public void add(final Section section) {
        if (addToEmpty(section)) {
            return;
        }
        if (addToFirst(section)) {
            return;
        }
        if (addToLast(section)) {
            return;
        }
        if (addInBetween(section)) {
            return;
        }
        throw new IllegalArgumentException();
    }

    private boolean addToEmpty(final Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return SECTION_ADDED;
        }
        return SECTION_NOT_ADDED;
    }

    private boolean addToFirst(final Section section) {
        final Section firstSection = getFirstSection();
        if (Objects.equals(section.getDownStation(), firstSection.getUpStation())) {
            sections.add(SECTIONS_START_AT, section);
            return SECTION_ADDED;
        }
        return SECTION_NOT_ADDED;
    }

    private boolean addToLast(final Section section) {
        final Section lastSection = getLastSection();
        if (Objects.equals(section.getUpStation(), lastSection.getDownStation())) {
            sections.add(sections.size(), section);
            return SECTION_ADDED;
        }
        return SECTION_NOT_ADDED;
    }

    private boolean addInBetween(final Section section) {
        if (addInBetweenAsUpSection(section)) {
            return SECTION_ADDED;
        }
        if (addInBetweenAsDownSection(section)) {
            return SECTION_ADDED;
        }
        return SECTION_NOT_ADDED;
    }

    private boolean addInBetweenAsUpSection(final Section section) {
        final Section sectionWithMatchingUpStation = sections.stream()
            .filter(s -> Objects.equals(s.getUpStation(), section.getUpStation()))
            .findFirst()
            .orElse(null);
        if (Objects.nonNull(sectionWithMatchingUpStation)) {
            sectionWithMatchingUpStation.adjustUpStation(section);
            sections.add(sections.indexOf(sectionWithMatchingUpStation), section);
            return SECTION_ADDED;
        }
        return SECTION_NOT_ADDED;
    }

    private boolean addInBetweenAsDownSection(final Section section) {
        final Section sectionWithMatchingDownStation = sections.stream()
            .filter(s -> Objects.equals(s.getDownStation(), section.getDownStation()))
            .findFirst()
            .orElse(null);
        if (Objects.nonNull(sectionWithMatchingDownStation)) {
            sectionWithMatchingDownStation.adjustDownStation(section);
            sections.add(sections.indexOf(sectionWithMatchingDownStation) + SECTIONS_UNIT, section);
            return SECTION_ADDED;
        }
        return SECTION_NOT_ADDED;
    }

    public List<Station> getStationsInOrder() {
        if (sections.isEmpty()) {
            return Collections.emptyList();
        }
        final Stream<Station> stations = sections.stream().map(Section::getUpStation);
        final Stream<Station> lastStation = Stream.of(getLastStation());
        return Stream.concat(stations, lastStation).collect(Collectors.toList());
    }

    public int size() {
        return sections.size();
    }

    private Section getFirstSection() {
        return sections.get(SECTIONS_START_AT);
    }

    private Section getLastSection() {
        Section theSection = getFirstSection();
        Optional<Section> maybeSection = Optional.ofNullable(theSection);
        while (maybeSection.isPresent()) {
            final Section tmpSection = maybeSection.get();
            maybeSection = sections.stream()
                .filter(s -> Objects.equals(tmpSection.getDownStation(), s.getUpStation()))
                .findFirst();
            theSection = tmpSection;
        }
        return theSection;
    }

    private Station getLastStation() {
        return sections.get(size() - SECTIONS_UNIT).getDownStation();
    }
}
