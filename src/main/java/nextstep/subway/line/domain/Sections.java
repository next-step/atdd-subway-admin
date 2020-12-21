package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "line_id")
    private List<Section> sections = new ArrayList<>();

    public int size() {
        return sections.size();
    }

    public void add(final Section section) {
        this.sections.add(section);
    }

    public List<Long> getStationIds() {
        return sections.stream()
                .flatMap(it -> it.getStationIds().stream())
                .distinct()
                .collect(Collectors.toList());
    }
}
