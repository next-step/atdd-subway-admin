package nextstep.subway.line.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.Objects;

public class LineStationResponse {
    private Long id;
    private String name;

    private LineStationResponse() {
    }

    private LineStationResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static LineStationResponse of(Long id, String name) {
        return new LineStationResponse(id, name);
    }

    public static LineStationResponse from(StationResponse stationResponse) {
        return new LineStationResponse(stationResponse.getId(), stationResponse.getName());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineStationResponse that = (LineStationResponse) o;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
