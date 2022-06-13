package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import nextstep.subway.domain.Line;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();
    private List<SectionResponse> sections = new ArrayList<>();

    public static LineResponse of(final Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                line.getLineStations().stations(),
                line.getSections());
    }

    public LineResponse() {
    }

    public LineResponse(final Long id,
                        final String name,
                        final String color,
                        final List<StationResponse> stations,
                        final List<SectionResponse> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
        this.sections = sections;
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

    public List<SectionResponse> getSections() {
        return sections;
    }

    @Override
    public String toString() {
        return "LineResponse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", stations=" + stations +
                ", sections=" + sections +
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
                && Objects.equals(stations, that.stations)
                && Objects.equals(sections, that.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color, stations, sections);
    }
}
