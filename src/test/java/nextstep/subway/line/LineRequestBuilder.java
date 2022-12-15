package nextstep.subway.line;

import java.util.HashMap;
import java.util.Map;

public class LineRequestBuilder {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public LineRequestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public LineRequestBuilder withColor(String color) {
        this.color = color;
        return this;
    }

    public LineRequestBuilder withUpStation(Long id) {
        this.upStationId = id;
        return this;
    }

    public LineRequestBuilder withDownStation(Long id) {
        this.downStationId = id;
        return this;
    }

    public LineRequestBuilder withDistance(int distance) {
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
