package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<LineStation> stations;

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getStations());
    }

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = LineStation.toList(stations);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<LineStation> getStations() {
        return stations;
    }

    private static class LineStation {
        private final Long id;
        private final String name;

        public static LineStation to(Station station) {
            return new LineStation(station);
        }

        public static List<LineStation> toList(List<Station> stations) {
            return stations.stream().map(LineStation::to).collect(Collectors.toList());
        }

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
