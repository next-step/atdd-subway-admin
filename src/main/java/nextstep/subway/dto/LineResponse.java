package nextstep.subway.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse(Long id, String name, String color, Sections sections) {
        this.id = id;
        this.name = name;
        this.color = color;

        sections.order();
        this.stations = sections.getStations()
                .stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getSections());
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

    public List<StationResponse> getStations() {
        return stations;
    }

    private static class StationResponse {

        public Long id;
        public String name;

        private StationResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        private static StationResponse from(Station station) {
            return new StationResponse(station.getId(), station.getName());
        }

        public Long getId() {
            return id;
        }
    }

}
