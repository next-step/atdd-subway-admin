package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
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
}
