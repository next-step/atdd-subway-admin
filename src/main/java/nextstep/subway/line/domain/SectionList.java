package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.section.domain.Section;

@Embeddable
public class SectionList {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private List<Section> sections;

    public SectionList() {
        this.sections = new ArrayList<>();
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Section> getSortedList() {
        return this.sections.stream()
            .sorted()
            .collect(Collectors.toList());
    }
}
