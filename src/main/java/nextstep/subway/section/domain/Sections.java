package nextstep.subway.section.domain;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
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
            throw new IllegalArgumentException("등록하려는 역이 모두 존재합니다.");
        }
    }

    private void validateHasNotBothStations(Section newSection) {
        if (hasNotBothStations(newSection)) {
            throw new IllegalArgumentException("상행성과 하행선 모두 존재하지 않습니다.");
        }
    }

    private boolean hasNotBothStations(Section section) {
        List<Station> stations = getStations();
        return section.findStations()
                .stream()
                .noneMatch(stations::contains);
    }
}
