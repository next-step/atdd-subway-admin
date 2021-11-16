package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LinesResponse {

    private List<LineResponse> list;

    public LinesResponse() {
    }

    public LinesResponse(final List<Line> list) {
        this.list = list.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public List<LineResponse> getList() {
        return list;
    }

    public int size() {
        return list.size();
    }

}
