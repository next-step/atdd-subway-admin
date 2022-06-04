package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "line",
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
                .flatMap(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .collect(Collectors.toList());
    }

    public List<Section> getSections() {
        return sections;
    }
}
