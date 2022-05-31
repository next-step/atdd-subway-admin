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
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections valueOf(List<Section> sections) {
        return new Sections(sections);
    }

    public void addSection(Section section) {
        if (!isSectionsEmpty()) {
            update(section);
        }
        sections.add(section);
    }

    public Distance distance() {
        return Distance.valueOf(sections.stream()
                .mapToInt(section -> section.distance().distance())
                .reduce(0, Integer::sum));
    }

    public List<Station> stations() {
        return sections.stream()
                .map(Section::stations)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Section> sections() {
        return sections;
    }

    private void update(Section newSection) {
        sections.forEach(section -> section.update(newSection));
    }

    private boolean isSectionsEmpty() {
        return sections.isEmpty();
    }
}