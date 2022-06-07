package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.Stations;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final Stations stations;

    private LineResponse(final Long id, final String name, final String color, final List<Station> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = Stations.of(stations);
    }

    public static LineResponse from(final Line line) {
        return new LineResponse(line.getId(), line.getName(), line.getColor(),
                createStations(line.getUpStation(), line.getDownStation()));
    }

    private static List<Station> createStations(final Station upStation, final Station downStation) {
        return Arrays.asList(upStation, downStation);
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
        return stations.getStationResponse();
    }

    @Override
    public String toString() {
        return "LineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color=" + color +
                ", stations=" + stations +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LineResponse that = (LineResponse) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(color, that.color) && Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations);
    }
}
