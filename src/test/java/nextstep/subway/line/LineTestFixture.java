package nextstep.subway.line;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationResponse;

public class LineTestFixture {

    private LineTestFixture() {
        throw new UnsupportedOperationException();
    }

    public static LineRequest 신분당선_요청_데이터(StationResponse upStationResponse, StationResponse downStationResponse, int distance) {
        return new LineRequest("신분당선", "bg-red-600", upStationResponse.getId(), downStationResponse.getId(), distance);
    }

    public static LineRequest 구분당선_요청_데이터(StationResponse upStationResponse, StationResponse downStationResponse, int distance) {
        return new LineRequest("구분당선", "bg-blue-600", upStationResponse.getId(), downStationResponse.getId(), distance);
    }

    public static LineRequest 이호선_요청_데이터(StationResponse upStationResponse, StationResponse downStationResponse, int distance) {
        return new LineRequest("2호선", "bg-green-600", upStationResponse.getId(), downStationResponse.getId(), distance);
    }
}
