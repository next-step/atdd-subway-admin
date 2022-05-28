package nextstep.subway.dto;

import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;

import java.util.ArrayList;
import java.util.List;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationDto> stations;

    public static LineResponse of(Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(), line.getUpStation(), line.getDownStation());
    }

    public LineResponse() {

    }

    public LineResponse(Long id, String name, String color, Station upStation, Station downStation) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = new ArrayList<>();

        stations.add(StationDto.of(upStation));
        stations.add(StationDto.of(downStation));
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

    public List<StationDto> getStations() {
        return stations;
    }

    public static class StationDto {
        private Long id;
        private String name;

        public static StationDto of(Station station) {
            return new StationDto(station.getId(), station.getName());
        }

        public StationDto() {
        }

        private StationDto(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}
