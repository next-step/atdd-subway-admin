package nextstep.subway.dto;

import nextstep.subway.domain.Line;

import java.util.List;

public class LineResponse {
    private long id;
    private String name;
    private String color;
    private List<StationOfLineResponse> stations;

    public LineResponse(Line line) {
    }

    public long getId() {
        return id;
    }

    public static class StationOfLineResponse {
        private Long id;
        private String name;
    }
}
