package nextstep.subway.line.dto;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.line.domain.Line;

public class LineResponseDto {
    private final Long id;
    private final String name;
    private final String color;
    private final List<FinalStationDto> stations;

    private LineResponseDto(Long id, String name, String color, List<FinalStationDto> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponseDto from(Line line) {
        List<FinalStationDto> stations = Arrays.asList(FinalStationDto.from(line.getUpStation()),
                FinalStationDto.from(line.getDownStation()));
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

    public List<FinalStationDto> getStations() {
        return stations;
    }
}
