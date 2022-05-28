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

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<LineStation> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<LineStation> stations = line.getStations().stream()
                .map(LineStation::of)
                .collect(Collectors.toList());
        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
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

    static class LineStation {
        private Long id;
        private String name;

        public LineStation() {
        }

        public LineStation(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public static LineStation of(Station station) {
            return new LineStation(station.getId(), station.getName());
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
