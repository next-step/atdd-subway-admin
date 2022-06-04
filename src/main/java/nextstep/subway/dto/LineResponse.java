package nextstep.subway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;


    public LineResponse(Long id, String name, String color, List<StationResponse> stationResponses) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stationResponses;
    }

    public static LineResponse of(Line line) {
        List<Station> stations = new ArrayList<>();
        for (Section section : line.getSections()) {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        }

        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                stations.stream()
                        .distinct()
                        .map(StationResponse::of)
                        .collect(Collectors.toList())
        );
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

    @JsonIgnore
    public List<String> getStationNames() {
        return stations.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());
    }
}
