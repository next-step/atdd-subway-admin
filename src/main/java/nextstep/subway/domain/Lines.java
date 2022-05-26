package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LinesResponse;

public class Lines {

    private final List<Line> lines;

    public Lines(List<Line> lines) {
        this.lines = lines;
    }

    public LinesResponse getResponse() {
        List<LineResponse> linesResponse = lines.stream().map(LineResponse::new).collect(Collectors.toList());
        return new LinesResponse(linesResponse);
    }

}
