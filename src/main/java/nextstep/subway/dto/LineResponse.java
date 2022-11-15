package nextstep.subway.dto;

import com.google.common.collect.Lists;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    private LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                Lists.newArrayList(
                        StationResponse.from(line.getUpStation()),
                        StationResponse.from(line.getDownStation())
                )
        );
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
    }

}
