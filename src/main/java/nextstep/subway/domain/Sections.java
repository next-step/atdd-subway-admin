package nextstep.subway.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

@Embeddable
public class Sections {

    @OneToMany(cascade =  CascadeType.ALL)
    @JoinColumn(name = "lineId")
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = requireNonNull(sections, "sections");
    }

    protected Sections() {
    }

    public void add(Section section) {
        requireNonNull(section, "section");
        sections.add(section);
    }

    public List<Station> getStations() {
        return sections.stream()
                       .flatMap(s -> Stream.of(s.getUpStation(), s.getDownStation()))
                       .collect(Collectors.toList());
    }
}
