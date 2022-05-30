package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

@Embeddable
public class Sections {

    private static final String NOT_LINKABLE_SECTION = "둘중 하나의 역이 등록이 되어 있어야 합니다.";
    private static final String SECTION_DUPLICATION_ERROR = "상, 하행 지하철역이 같은 구간 이미 등록 되어 있습니다.";

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections newInstance() {
        return new Sections();
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public List<StationResponse> getAllStations() {
        return findAllStations().stream().
                map(StationResponse::of).
                collect(Collectors.toList());
    }

    private Set<Station> findAllStations() {
        return sections.stream().
                flatMap(s -> s.getBothStations().stream()).
                collect(Collectors.toSet());
    }

    public void add(Section newSection) {
        if (sections.isEmpty()) {
            sections.add(newSection);
            return;
        }
        validateLinkableSection(newSection);
        validateDuplicateSection(newSection);
        sections.forEach(s -> s.modifySectionFor(newSection));
        sections.add(newSection);
    }

    private void validateDuplicateSection(Section section) {
        if (sections.stream().anyMatch(s -> s.isSameBothStation(section))) {
            throw new IllegalArgumentException(SECTION_DUPLICATION_ERROR);
        }
    }

    private void validateLinkableSection(Section section) {
        if (isNotAddedStation(section.getDownStation())
                && isNotAddedStation(section.getUpStation())) {
            throw new IllegalArgumentException(NOT_LINKABLE_SECTION);
        }
    }

    private boolean isNotAddedStation(Station station) {
        return !findAllStations().contains(station);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
