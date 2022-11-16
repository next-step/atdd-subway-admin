package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    private static final String SECTION_DUPLICATION_ERROR = "상행선과 하행선이 모두 존재합니다.";

    private static final String SECTION_NOT_EXIST_ERROR = "상행선과 하행선이 모두 존재하지 않습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        validateDuplicate(section);
        validateNotExist(section);
        sections.forEach(s -> s.updateStation(section));
        sections.add(section);
    }

    private void validateDuplicate(Section section) {
        if (getStations().containsAll(section.getStations())) {
            throw new IllegalArgumentException(SECTION_DUPLICATION_ERROR);
        }
    }

    private void validateNotExist(Section section) {
        if (isNotExist(section)) {
            throw new IllegalArgumentException(SECTION_NOT_EXIST_ERROR);
        }
    }

    private boolean isNotExist(Section section) {
        List<Station> stations = getStations();
        return section.getStations()
            .stream()
            .noneMatch(stations::contains);
    }

    public void remove(Station station) {
        Optional<Section> upStationSection = findUpStationSection(station);
        Optional<Section> downStationSection = findDownStationSection(station);

        upStationSection.ifPresent(sections::remove);
        downStationSection.ifPresent(sections::remove);
    }

    private Optional<Section> findUpStationSection(Station station) {
        return sections.stream()
            .filter(section -> section.isUpStation(station))
            .findFirst();
    }

    private Optional<Section> findDownStationSection(Station station) {
        return sections.stream()
            .filter(section -> section.isDownStation(station))
            .findFirst();
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(Section::getStations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }
}
