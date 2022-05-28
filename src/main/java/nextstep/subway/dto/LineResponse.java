package nextstep.subway.dto;

import nextstep.subway.domain.Line;
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
        line.getSections().forEach(section -> {
            stations.add(section.getUpStation());
            stations.add(section.getDownStation());
        });
        return new LineResponse(line.getId(),
                line.getName(),
                line.getColor(),
                stations.stream().map(StationResponse::of)
                        .collect(Collectors.toList())
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
