package nextstep.subway.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

public class LineResponse extends BaseDto {
    private final Long id;
    private final String name;
    private final String color;
    private final int distance;

    private final List<StationResponse> stations;

    private LineResponse(Long id, String name, String color, int distance, List<StationResponse> stations,
                         LocalDateTime createdDate,
                         LocalDateTime modifiedDate) {
        super(createdDate, modifiedDate);
        this.id = id;
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = line.sections().stations().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return new LineResponse(line.id(), line.name(), line.color(), line.distance().distance(), stations,
                line.getCreatedDate(),
                line.getModifiedDate());
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

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineResponse that = (LineResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
