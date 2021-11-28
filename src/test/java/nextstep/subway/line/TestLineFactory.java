package nextstep.subway.line;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.http.HttpMethod;

import static nextstep.subway.station.TestStationFactory.역_생성;
import static nextstep.subway.utils.TestRequestFactory.요청;

public class TestLineFactory {
    public static LineResponse 노선_생성(String upStationName, String downStationName, String lineName, String color, int distance) {
        StationResponse upStation = 역_생성(upStationName);
        StationResponse downStation = 역_생성(downStationName);

        LineRequest lineRequestWithStations = new LineRequest(lineName, color, upStation.getId(), downStation.getId(), distance);
        return 요청(HttpMethod.POST, "/lines", lineRequestWithStations).body().as(LineResponse.class);
    }
}
