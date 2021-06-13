package nextstep.subway.line.domain;

import nextstep.subway.section.domain.Section;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line")
    private final List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public List<Long> ids() {
        return sections.stream().map(Section::getId)
                .collect(Collectors.toList());
    }
}
