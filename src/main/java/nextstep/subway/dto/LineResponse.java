package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponse {
    private long id;
    private String name;
    private String color;
    private List<StationOfLineResponse> stations;

    public LineResponse(Line line) {
        id = line.getId();
        name = line.getName();
        color = line.getColor();
        stations =
                line.getLineStations()
                        .stream()
                        .map(lineStation -> new StationOfLineResponse(
                                lineStation.getStation().getId(),
                                lineStation.getStation().getName()
                        ))
                        .collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public static class StationOfLineResponse {
        private Long id;
        private String name;

        public StationOfLineResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
