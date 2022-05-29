package nextstep.subway.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StationResponses {
    private List<StationResponse> list;

    public StationResponses() {
        this.list = new ArrayList<>();
    }

    public StationResponses(List<StationResponse> list) {
        this.list = list;
    }

    public List<StationResponse> getList() {
        return Collections.unmodifiableList(list);
    }
}
