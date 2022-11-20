package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nextstep.subway.domain.Line;

public class LineResponse {

    private Long id;
    private String name;
    private String color;
    private List<StationResponse> stations;

    public LineResponse() {
    }

    public LineResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public static LineResponse of(Line line) {
        List<StationResponse> stations = createStations(line);
        stations.sort((o1, o2) -> o1.getId() > o2.getId() ? 1 : -1);

        return new LineResponse(line.getId(), line.getName(), line.getColor(), stations);
    }

    private static List<StationResponse> createStations(Line line) {
        Set<StationResponse> stationsSet = new HashSet<>();
        line.getSectionList().forEach(section -> {
            stationsSet.add(StationResponse.of(section.getUpStation()));
            stationsSet.add(StationResponse.of(section.getDownStation()));
        });
        return new ArrayList<>(stationsSet);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }
}
