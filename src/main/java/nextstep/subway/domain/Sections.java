package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import nextstep.subway.exception.NoRelationStationException;

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
            .map(Section::stations)
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());
    }

    public void addSection(Section newSection) {
        Section section1 = sections.stream()
            .filter(section -> section.hasRelation(newSection))
            .findAny()
            .orElse(null);

        if (section1 == null) {
            throw new NoRelationStationException();
        }

        sections.stream()
            .filter(section -> section.prevSection(newSection))
            .findFirst()
            .ifPresent(prevSection -> prevSection.betweenBefore(newSection));

        sections.stream()
            .filter(section -> section.afterSection(newSection))
            .findFirst()
            .ifPresent(prevSection -> prevSection.betweenAfter(newSection));

        sections.add(newSection);
    }

    public List<Section> getSections() {
        return sections;
    }
}
