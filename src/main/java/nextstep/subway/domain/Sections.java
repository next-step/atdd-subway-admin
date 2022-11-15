package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    protected Sections() {}

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::findStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }

        validate(newSection);
        sections.forEach(section -> section.update(newSection));
        sections.add(newSection);
    }

    private void validate(Section section) {
        validateHasBothStations(section);
        validateHasNotBothStations(section);
    }

    private void validateHasBothStations(Section section) {
        if (getStations().containsAll(section.findStations())) {
            throw new IllegalArgumentException("추가하려는 역이 모두 존재합니다.");
        }
    }

    private void validateHasNotBothStations(Section section) {
        if (hasNotBothStations(section)) {
            throw new IllegalArgumentException("상행성 하행선 모두 존재하지 않습니다.");
        }
    }

    private boolean hasNotBothStations(Section section) {
        List<Station> stations = getStations();
        return section.findStations()
                .stream()
                .noneMatch(stations::contains);
    }

    public void delete(Station station) {
        Optional<Section> prevSection = findPrevSection(station);
        Optional<Section> nextSection = findNextSection(station);

        if (isMiddleSection(prevSection, nextSection)) {
            deleteMiddleSection(prevSection.get(), nextSection.get());
            return;
        }

        deleteEndSection(prevSection, nextSection);
    }

    private Optional<Section> findPrevSection(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualDownStation(station))
                .findFirst();
    }

    private Optional<Section> findNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualUpStation(station))
                .findFirst();
    }

    private boolean isMiddleSection(Optional<Section> prevSection, Optional<Section> nextSection) {
        return prevSection.isPresent() && nextSection.isPresent();
    }

    private void deleteMiddleSection(Section prevSection, Section nextSection) {
        sections.add(prevSection.merge(nextSection));
        sections.remove(prevSection);
        sections.remove(nextSection);
    }

    private void deleteEndSection(Optional<Section> prevSection, Optional<Section> nextSection) {
        prevSection.ifPresent(sections::remove);
        nextSection.ifPresent(sections::remove);
    }
}
