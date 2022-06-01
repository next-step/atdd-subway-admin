package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<LineStation> stations = new ArrayList<>();

    public LineResponse(Long id, String name, String color, List<Station> orderedUpToDownStations) {
        this.id = id;
        this.name = name;
        this.color = color;

        for (Station station : orderedUpToDownStations) {
            stations.add(new LineStation(station));
        }
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<LineStation> getStations() {
        return this.stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStationOrderedUpToDown());
    }

    private class LineStation {
        private Long id;
        private String name;

        public LineStation(Station station) {
            this.id = station.getId();
            this.name = station.getName();
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
