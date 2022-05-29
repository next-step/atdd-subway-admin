package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineResponses {
    private List<LineResponse> list;

    public LineResponses() {
        list = new ArrayList<>();
    }

    public LineResponses(List<LineResponse> list) {
        this.list = list;
    }

    public List<LineResponse> getList() {
        return Collections.unmodifiableList(list);
    }
}
