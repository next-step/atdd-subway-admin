package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

class LineStationAcceptanceTestFixture {

    @LocalServerPort
    int port;

    Station 강남역;
    Station 역삼역;
    Line _2호선;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        강남역 = StationAcceptanceTest.지하철역_생성("강남역").as(Station.class);
        역삼역 = StationAcceptanceTest.지하철역_생성("역삼역").as(Station.class);
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
}
