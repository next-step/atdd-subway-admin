package nextstep.subway.lineStation;

import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;

import java.util.ArrayList;
import java.util.List;

public class LineAndLineStation {
    private LineResponse lineResponse;
    private List<StationResponse> stationResponses;

    public LineAndLineStation(LineResponse lineResponse, List<StationResponse> stationResponses) {
        this.lineResponse = lineResponse;
        this.stationResponses = new ArrayList<>(stationResponses);
    }

    public LineResponse getLineResponse() {
        return lineResponse;
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }
}
