package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

class LineStationAcceptanceTestFixture {

    @LocalServerPort
    int port;

    Station 서초역;
    Station 강남역;
    Line _2호선;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        서초역 = StationAcceptanceTest.지하철역_생성("서초역").as(Station.class);
        강남역 = StationAcceptanceTest.지하철역_생성("강남").as(Station.class);
        _2호선 = LineAcceptanceTest.지하철_노선_생성("2호선", "green").as(Line.class);
    }

    protected ExtractableResponse<Response> 구간등록(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId + "");
        params.put("downStationId", downStationId + "");
        params.put("distance", distance + "");

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 구간목록조회(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/" + lineId + "/sections")
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 구간정보조회(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/" + lineId + "/sections" + "/" + stationId)
                .then().log().all()
                .extract();
    }
}
