package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void add(Section section) {
        this.sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        Section upEndSection = findUpEndSection();
        stations.add(upEndSection.getUpStation());

        Section nextSection = upEndSection;
        while (nextSection != null) {
            stations.add(nextSection.getDownStation());
            nextSection = findSectionByNextUpStation(nextSection.getDownStation());
        }

        return stations;
    }

    private Section findUpEndSection() {
        List<Station> downStations = this.sections.stream()
                .map(it -> it.getDownStation())
                .collect(Collectors.toList());

        return this.sections.stream()
                .filter(it -> !downStations.contains(it.getUpStation()))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    private Section findSectionByNextUpStation(Station station) {
        return this.sections.stream()
                .filter(it -> it.getUpStation() == station)
                .findFirst()
                .orElse(null);
    }
}
