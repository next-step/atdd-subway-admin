package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        mappedBy = "line",
        orphanRemoval = true
    )
    private List<Section> sections = new ArrayList<>();

    protected Sections() {
    }

    public Sections(List<Section> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public Sections add(final Section section) {
        final List<Section> newSections = new ArrayList<>(sections);
        newSections.add(section);
        return new Sections(newSections);
    }

    public List<Station> getStations() {
        final Stream<Station> stations = sections.stream().map(Section::getUpStation);
        final Stream<Station> lastStation = Stream.of(getLastStation());
        return Stream.concat(stations, lastStation).collect(Collectors.toList());
    }

    private Station getLastStation() {
        return sections.get(sections.size() - 1).getDownStation();
    }
}
