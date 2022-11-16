package nextstep.subway.section.domain;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.common.exception.ErrorMessageConstant;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = Lists.newArrayList();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> getStations() {
        return sections.stream()
                .map(Section::findStations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
    public static Sections create() {
        return new Sections();
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
        validateHasStations(section);
        validateHasNotBothStations(section);
    }

    private void validateHasStations(Section newSection) {
        if (new HashSet<>(getStations()).containsAll(newSection.findStations())) {
            throw new IllegalArgumentException(ErrorMessageConstant.EXISTS_STATION);
        }
    }

    private void validateHasNotBothStations(Section newSection) {
        if (hasNotBothStations(newSection)) {
            throw new IllegalArgumentException(ErrorMessageConstant.EXISTS_UP_STATION_AND_DOWN_STATION);
        }
    }

    private boolean hasNotBothStations(Section section) {
        List<Station> stations = getStations();
        return section.findStations()
                .stream()
                .noneMatch(stations::contains);
    }

    public void delete(Station station) {
        Optional<Section> prevSection = getPrevSection(station);
        Optional<Section> nextSection = getNextSection(station);
        if (isMiddleSection(prevSection, nextSection)) {
            deleteMiddleSection(prevSection.get(), nextSection.get());
            return;
        }
        deleteEndSection(prevSection, nextSection);
    }

    private void deleteEndSection(Optional<Section> prevSection, Optional<Section> nextSection) {
        prevSection.ifPresent(sections::remove);
        nextSection.ifPresent(sections::remove);
    }

    private boolean isMiddleSection(Optional<Section> prevSection, Optional<Section> nextSection) {
        return prevSection.isPresent() && nextSection.isPresent();
    }

    private void deleteMiddleSection(Section prevSection, Section nextSection) {
        sections.add(prevSection.merge(nextSection));
        sections.remove(prevSection);
        sections.remove(nextSection);
    }

    private Optional<Section> getPrevSection(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualDownStation(station))
                .findFirst();
    }

    private Optional<Section> getNextSection(Station station) {
        return sections.stream()
                .filter(section -> section.isEqualUpStation(station))
                .findFirst();
    }
}
