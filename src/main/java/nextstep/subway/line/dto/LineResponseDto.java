package nextstep.subway.line.dto;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.line.domain.Line;

public class LineResponseDto {
    private Long id;
    private String name;
    private String color;
    private List<StationDto> stations;

    private LineResponseDto() {
    }

    private LineResponseDto(Long id, String name, String color, List<StationDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponseDto from(Line line) {
        List<StationDto> stations = line.getStations().stream()
                .map(StationDto::from)
                .collect(Collectors.toList());
        return new LineResponseDto(line.getId(), line.getName(), line.getColor(), stations);
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
}
