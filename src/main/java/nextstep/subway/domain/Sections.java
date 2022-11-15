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
        Section prevSection = sections.stream()
            .filter(section -> section.prevSection(newSection))
            .findFirst()
            .get();
        prevSection.change(newSection);
        sections.add(newSection);
    }
}
