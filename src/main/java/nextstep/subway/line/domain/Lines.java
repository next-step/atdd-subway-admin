package nextstep.subway.line.domain;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Lines {
    private List<Line> lines;

    private Lines() {
        this.lines = new ArrayList<>();
    }

    private Lines(List<Line> lines) {
        this.lines = lines;
    }

    public static Lines from(List<Line> lines) {
        return new Lines(lines);
    }

    public List<LineResponse> toLineResponses() {
        return lines.stream()
                .map(Line::toLineResponse)
                .collect(Collectors.toList());
    }
}
