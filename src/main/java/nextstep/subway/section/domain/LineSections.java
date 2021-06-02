package nextstep.subway.section.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

import static java.util.stream.Collectors.toList;

@Embeddable
public class LineSections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private final List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> toStations() {
        return sections.stream()
                       .flatMap(
                           section -> Stream.of(section.getUpStation(), section.getDownStation()))
                       .distinct()
                       .sorted(Comparator.comparingLong(Station::getId))
                       .collect(toList());
    }
}
