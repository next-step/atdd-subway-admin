package nextstep.subway.line.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    private List<Section> sections = new ArrayList<>();

    public void add(Section section) {
        sections.add(section);
    }

    public List<Station> getStations() {
        List<Station> stations = new ArrayList<>();
        for (Section section : this.sections) {
            addStationsInSection(stations, section);
        }
        stations.sort((s1, s2) -> (int) (s1.getId() - s2.getId()));
        return stations;
    }

    private void addStationsInSection(List<Station> stations, Section section) {
        if (!stations.contains(section.getUpStation())) {
            stations.add(section.getUpStation());
        }
        if (!stations.contains(section.getDownStation())) {
            stations.add(section.getDownStation());
        }
    }
}
