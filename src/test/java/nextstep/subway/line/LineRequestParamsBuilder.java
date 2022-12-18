package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

public class LineRequestParamsBuilder {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequestParamsBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LineRequestParamsBuilder withColor(String color) {
        this.color = color;
        return this;
    }

    public LineRequestParamsBuilder withUpStation(Long id) {
        this.upStationId = id;
        return this;
    }

    public LineRequestParamsBuilder withDownStation(Long id) {
        this.downStationId = id;
        return this;
    }

    public LineRequestParamsBuilder withDistance(int distance) {
        this.distance = distance;
        return this;
    }

    public Map<String, String> build() {
        Map<String, String> params = new HashMap<>();
        params.put("name", this.name);
        params.put("color", this.color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", Integer.toString(distance));
        return params;
    }
}
