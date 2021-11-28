package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class LinesResponse {

    private List<LineResponse> lines;

    protected LinesResponse() {
        this.lines = new ArrayList<>();
    }

    private LinesResponse(List<LineResponse> lines) {
        this.lines = lines;
    }

    public List<LineResponse> getLines() {
        return lines;
    }

    public static LinesResponse of(List<Line> lines) {

        if (CollectionUtils.isEmpty(lines)) {
            return new LinesResponse();
        }

        List<LineResponse> converted = lines.stream()
                .map(LineResponse::of)
                .collect(toList());

        return new LinesResponse(converted);
    }
}
