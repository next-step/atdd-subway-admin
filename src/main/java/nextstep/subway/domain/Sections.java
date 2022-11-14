package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

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
            throw new IllegalArgumentException("상행선과 하행선이 모두 존재합니다.");
        }
    }

    private void validateNotExist(Section section) {
        if (isNotExist(section)) {
            throw new IllegalArgumentException("상행선과 하행선이 모두 존재하지 않습니다.");
        }
    }

    private boolean isNotExist(Section section) {
        List<Station> stations = getStations();
        return section.getStations()
            .stream()
            .noneMatch(stations::contains);
    }

    public List<Station> getStations() {
        return sections.stream()
            .map(Section::getStations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }
}
