package nextstep.subway.dto;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationDto> stations;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public LineResponse() {
    }

    public LineResponse(Line line, List<Station> stations) {
        this.id = line.getId();
        this.name = line.getName();
        this.color = line.getColor();
        this.stations = stations.stream().map(StationDto::new).collect(Collectors.toList());
        this.createdDate = line.getCreatedDate();
        this.modifiedDate = line.getModifiedDate();
    }

    public static LineResponse of(Line line, List<Station> stations) {
        return new LineResponse(line, stations);
    }

    public static class StationDto {
        private Long id;
        private String name;

        public StationDto() {
        }

        public StationDto(Station station) {
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

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }
}
