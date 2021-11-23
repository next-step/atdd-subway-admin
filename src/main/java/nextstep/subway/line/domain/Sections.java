package nextstep.subway.line.domain;

import static java.util.stream.Collectors.*;
import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.OneToMany;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", fetch = LAZY, cascade = ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(List<Section> sections, Line line) {
        this.sections = sections.stream()
                                .peek(section -> section.setLine(line))
                                .collect(toList());
    }

    public List<Section> getStations() {
        List<Section> collect = sections.stream().sorted().collect(toList());
        return Collections.unmodifiableList(collect);
    }

}
