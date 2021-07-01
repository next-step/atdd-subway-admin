package nextstep.subway.station.domain;

import nextstep.subway.line.domain.Sections;

import java.util.ArrayList;
import java.util.List;

public class Stations {

    private final List<Station> stations = new ArrayList<>();

    public Stations(Sections sections) {
        sections.getSections().forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
    }

    public List<Station> getStations() {
        return stations;
    }
}
