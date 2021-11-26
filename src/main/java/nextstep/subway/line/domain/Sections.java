package nextstep.subway.line.domain;

import nextstep.subway.exception.BadRequestException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getOrderedStations() {
        List<Station> orderedStations = new ArrayList<>();
        if (sections.size() != 0) {
            List<Station> headStations = sections.stream()
                .map(section -> Stream.of(section.getUpStation(), section.getDownStation()))
                .findFirst()
                .orElseThrow(BadRequestException::new)
                .collect(Collectors.toList());

            List<Station> nodeStations = sections.stream()
                    .map(Section::getDownStation)
                    .filter(downStation -> downStation != headStations.get(1))
                    .collect(Collectors.toList());

            orderedStations.addAll(headStations);
            orderedStations.addAll(nodeStations);
            orderedStations.sort(Comparator.comparing(Station::getId));
        }
        return orderedStations;
    }
}
