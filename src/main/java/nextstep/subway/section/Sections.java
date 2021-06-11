package nextstep.subway.section;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Embeddable
public class Sections {

    @OneToMany(mappedBy = "line", cascade = CascadeType.ALL)
    List<Section> sections = new LinkedList<>();

    public Sections() {
    }

    public void add(Section section) {
        if (sections.isEmpty()) {
            sections.add(section);
            return;
        }
        connectIfFront(section);
        connectIfLast(section);
    }

    private void connectIfLast(Section section) {
        if (sections.contains(section)) {
            return;
        }

        Station currentLastStation = sections.get(sections.size()-1).getDownStation();
        Station upStationOfInputSection = section.getUpStation();
        if(upStationOfInputSection.equals(currentLastStation)){
            sections.add(section);
        }
    }

    private void connectIfFront(Section section) {
        if(sections.contains(section)){
            return;
        }

        Station currentFrontMostStation = sections.get(0).getUpStation();
        Station downStationOfInputSection = section.getDownStation();
        if (downStationOfInputSection.equals(currentFrontMostStation)) {
            sections.add(0, section);
        }
    }

    public boolean contains(Section section) {
        return sections.contains(section);
    }

    public List<Station> sortedStations() {
        List<Station> stations = new ArrayList<>();

        for (Section section : sections) {
            addStation(stations, section);
        }
        return stations;
    }

    private List<Station> addStation(List<Station> stations, Section section) {
        if (!stations.contains(section.getUpStation())) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
            return stations;
        }

        stations.add(section.getDownStation());
        return stations;
    }
}
