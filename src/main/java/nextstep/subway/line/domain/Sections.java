package nextstep.subway.line.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import nextstep.subway.station.domain.Station;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST,
        CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    protected Sections() {

    }

    public Sections(Line line, Station upStation, Station downStation, int distance) {
        this.sections.add(new Section(line, upStation, downStation, distance));
    }


    public List<Section> getStations() {
        return sections;
    }

    public void add(Section newSection) {
        Station downStation = newSection.getDownStation();

        if (this.anyMatch(downStation)) {
            this.updateUpStation(newSection);
            this.sections.add(newSection);
            return;
        }
        this.sections.add(newSection);
    }

    private void updateUpStation(Section newSection) {
        getSectionByDownStation(newSection.getDownStation())
            .ifPresent(it -> it.updateUpStation(newSection.getDownStation(), newSection.getDistance()));
    }

    public Optional<Section> getSectionByDownStation(Station downStation) {
        return this.sections.stream()
            .filter(it -> it.getDownStation() == downStation)
            .findFirst();
    }

    public boolean anyMatch(Station station) {
        return Stream
            .concat(sections.stream().map(Section::getUpStation), sections.stream().map(Section::getDownStation))
            .anyMatch(it -> it == station);
    }
}
