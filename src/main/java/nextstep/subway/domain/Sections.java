package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.dto.SectionResponse;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    final List<Section> sections = new ArrayList<>();

    public void add(final Section section) {
        sections.add(section);
    }

    public List<SectionResponse> sections() {
        return sections
                .stream()
                .map(section -> SectionResponse.of(section))
                .collect(Collectors.toList());
    }
}
