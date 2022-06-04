package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;

    private String name;

    private String color;

    private List<StationResponse> stations = new ArrayList<>();

    public static LineResponse of(final Line line) {
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                stationResponses(line));
    }

    private static List<StationResponse> stationResponses(final Line line) {
        return line.getLineStations().getStations()
                .stream()
                .map(station -> StationResponse.of(station))
                .collect(Collectors.toList());
    }

    public LineResponse() {
    }

    public LineResponse(final Long id, final String name, final String color, final List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
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

    @Override
    public String toString() {
        return "LineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final LineResponse that = (LineResponse) o;
        return Objects.equals(id, that.id)
                && Objects.equals(name, that.name)
                && Objects.equals(color, that.color)
                && Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations);
    }
}
