package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<LineInStationResponse> stations;

    public LineResponse(Long id, String name, String color, List<LineInStationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), createStations(line));
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

    public List<LineInStationResponse> getStations() {
        return stations;
    }

    private static List<LineInStationResponse> createStations(Line line) {
        List<LineInStationResponse> responses = new ArrayList<>();
        if (line.getUpStation() != null) {
            responses.add(LineInStationResponse.of(line.getUpStation()));
        }
        if (line.getDownStation() != null) {
            responses.add(LineInStationResponse.of(line.getDownStation()));
        }
        return responses;
    }

    private static class LineInStationResponse {
        private final Long id;
        private final String name;

        public LineInStationResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public static LineInStationResponse of(Station station) {
            return new LineInStationResponse(station.getId(), station.getName());
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
