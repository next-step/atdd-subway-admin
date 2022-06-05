package nextstep.subway.dto.line;

import java.util.Arrays;
import java.util.List;
import nextstep.subway.domain.Line;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<FinalStation> stations;

    public LineResponse(Long id, String name, String color, List<FinalStation> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse from(Line line) {
        List<FinalStation> stations = Arrays.asList(FinalStation.from(line.getUpStation()),
                FinalStation.from(line.getDownStation()));
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

    public List<FinalStation> getStations() {
        return stations;
    }
}
