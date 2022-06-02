package nextstep.subway.dto;

import nextstep.subway.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineResponse {
    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations = new ArrayList<>();

    protected LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = new ArrayList<>();
        Sections sections = line.getSections();
        Section section = sections.getLineUpSection();
        while (section.getDownStation() != null) {
            Station station = new Station(section.getDownStation().getId(), section.getDownStation().getName());
            System.out.println("station: " + station);
            stations.add(StationResponse.of(station));
            section = sections.findSectionWithUpStation(section.getDownStation()).get();
        }
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

    public List<StationResponse> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineResponse that = (LineResponse) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
