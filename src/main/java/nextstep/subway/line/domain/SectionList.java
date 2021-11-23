package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.section.domain.Section;

@Embeddable
public class SectionList {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.ALL})
    private final List<Section> sections;

    public SectionList() {
        this.sections = new ArrayList<>();
    }

    public SectionList(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSortedList() {
        return Collections.unmodifiableList(
            this.sections.stream()
                .sorted()
                .collect(Collectors.toList()));
    }

    public boolean contains(Section section) {
        return this.sections.contains(section);
    }

    public void remove(Section section) {
        this.sections.remove(section);
    }
}
