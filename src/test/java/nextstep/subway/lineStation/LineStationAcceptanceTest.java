package nextstep.subway.lineStation;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선과 역의 관계")
public class LineStationAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @Test
    @DisplayName("지하철역과 노선 관계 목록 조회")
    void search_station_to_line() {
        //given
        //노선_등록_되어_있음
        지하철역_등록되어_있음();
        지하철_노선_등록(new LineRequest("1호선", "blue", 1L, 2L, 10));
        지하철_노선_등록(new LineRequest("2호선", "green", 3L, 2L, 10));

        //when
        ExtractableResponse<Response> stationsToLine = 지하철역_노선_관계_조회();

        //then
        List<LineResponse> line = stationsToLine.jsonPath().getList("line");
        assertThat(line.size()).isEqualTo(4);
    }

    @Test
    @DisplayName("지하철역은 여러 노선에 포함될 수 있음")
    void can_contains_station_to_line() {
        //given
        //노선_등록_되어_있음
        지하철역_등록되어_있음();
        지하철_노선_등록(new LineRequest("1호선", "blue", 1L, 2L, 10));
        지하철_노선_등록(new LineRequest("2호선", "green", 3L, 2L, 10));

        //when
        ExtractableResponse<Response> stationsToLine = 지하철역_노선_조회(2L);

        //then
        List<LineResponse> line = stationsToLine.jsonPath().getList(".");
        assertThat(line.size()).isEqualTo(2);
    }

    void 지하철역_등록되어_있음() {
        지하철역_등록(new StationRequest("구로역"));
        지하철역_등록(new StationRequest("신도림역"));
        지하철역_등록(new StationRequest("구디역"));
    }

    ExtractableResponse<Response> 지하철역_노선_관계_조회() {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lineStations")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철_노선_등록(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철역_등록(StationRequest stationRequest) {
        return RestAssured.given().log().all()
                .body(stationRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철역_노선_조회(Long id) {
        return RestAssured.given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lineStations/{id}", id)
                .then().log().all()
                .extract();
    }
}
