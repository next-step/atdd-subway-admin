package nextstep.subway.line;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.line.exception.NoRelationStationException;
import nextstep.subway.line.exception.SameStationException;
import nextstep.subway.station.Station;

@Embeddable
public class Sections {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(final Section... sections) {
        this(Arrays.asList(sections));
    }

    public Sections(final List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> stations() {
        return sections.stream()
            .sorted()
            .map(Section::stations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        validateNewSection(newSection);
        sections.stream()
            .filter(section -> section.hasSameUpOrDownStation(newSection))
            .findFirst()
            .ifPresent(section -> section.insertBetween(newSection));
        sections.add(newSection);
    }

    private void validateNewSection(Section newSection) {
        if (sections.stream()
            .noneMatch(section -> section.hasRelation(newSection))) {
            throw new NoRelationStationException();
        }

        if (sections.contains(newSection)) {
            throw new SameStationException();
        }
    }

    public List<Section> getSections() {
        return sections;
    }
}
