package nextstep.subway.line.domain;

import nextstep.subway.exception.NotFoundStationException;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public void add(Section section) {
        sections.add(section);
    }

    public List<Section> getSections() {
        return sections;
    }

    public Station findFirstStation() {
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        return sections.stream().filter(
                section -> !downStations.contains(section.getUpStation()))
                .findFirst()
                .orElseThrow(NotFoundStationException::new)
                .getUpStation();
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Station findStation = findFirstStation();
        while (!Objects.isNull(findStation)) {
            stations.add(findStation);
            final Station finalFindStation = findStation;
            Optional<Section> nextStation = sections.stream()
                    .filter(section -> section.isEqualsUpStation(finalFindStation))
                    .findFirst();

            findStation = nextStation
                    .orElse(new Section())
                    .getDownStation();
        }

        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sections sections1 = (Sections) o;
        return Objects.equals(sections, sections1.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sections);
    }
}
