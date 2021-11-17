package nextstep.subway.line.dto;

import nextstep.subway.line.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineResponseList {

    private List<LineResponse> lineResponseList;

    public LineResponseList() {
    }

    public LineResponseList(final List<Line> list) {
        this.lineResponseList = list.stream()
                .map(LineResponse::of)
                .collect(Collectors.toList());
    }

    public List<LineResponse> getList() {
        return lineResponseList;
    }

    public int size() {
        return lineResponseList.size();
    }

}
