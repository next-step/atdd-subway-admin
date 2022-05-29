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

    public LineResponse(Long id, String name, String color, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;

        if (upStation != null) {
            this.stations.add(new LineStation(upStation));
        }
        if (downStation != null) {
            this.stations.add(new LineStation(downStation));
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
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getUpStation(), line.getDownStation());
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
