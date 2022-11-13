package nextstep.subway.dto;

import java.util.List;
import java.util.Objects;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse(){}

    public LineResponse(String name, String color, List<StationResponse> stations) {
        this(null, name, color, stations);
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line){
        return new LineResponse(line.getId(), line.getName(), line.getColor(), null);
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LineResponse that = (LineResponse) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName())
                && Objects.equals(getColor(), that.getColor()) && Objects.equals(getStations(),
                that.getStations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getColor(), getStations());
    }
}
