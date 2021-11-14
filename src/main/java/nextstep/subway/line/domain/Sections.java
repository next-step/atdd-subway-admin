package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public Sections() {
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();

        Station upStation = getUpStation();
        stations.add(upStation);

        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> nextSection = getNextSectionByUpStation(finalUpStation);
            if (!nextSection.isPresent()) {
                break;
            }
            upStation = nextSection.get().getDownStation();
            stations.add(upStation);
        }
        return stations;
    }

    private Station getUpStation() {
        Station upStation = getFirstUpStation();
        while (upStation != null) {
            Station finalUpStation = upStation;
            Optional<Section> downStation = getNextSectionByDownStation(finalUpStation);
            if (!downStation.isPresent()) {
                break;
            }
            upStation = downStation.get().getUpStation();
        }
        return upStation;
    }

    private Optional<Section> getNextSectionByUpStation(Station finalUpStation) {
        return sections.stream()
                .filter(section -> section.isUpStation(finalUpStation))
                .findFirst();
    }

    private Optional<Section> getNextSectionByDownStation(Station finalUpStation) {
        return sections.stream()
                .filter(section -> section.isDownStation(finalUpStation))
                .findFirst();
    }

    private Station getFirstUpStation() {
        return sections.get(0).getUpStation();
    }

    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    public void add(Section section) {
        this.sections.add(section);
    }
}
