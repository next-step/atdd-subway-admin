package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;


import nextstep.subway.line.dto.LineCreateRequest;
import nextstep.subway.line.dto.LineResponse;


import nextstep.subway.line.dto.LineStationResponse;
import nextstep.subway.line.dto.LineUpdateRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineCreateRequest lineTwoCreateRequest;
    private LineCreateRequest lineThreeCreateRequest;

    @BeforeEach
    public void setUpTest() {

        StationResponse response1 = StationAcceptanceTest.지하철_역_등록되어_있음("강남역").as(StationResponse.class);
        StationResponse response2 = StationAcceptanceTest.지하철_역_등록되어_있음("삼성역").as(StationResponse.class);

        lineTwoCreateRequest = new LineCreateRequest("2호선", "green", response1.getId(), response2.getId(), 10);

        StationResponse response3 = StationAcceptanceTest.지하철_역_등록되어_있음("잠원역").as(StationResponse.class);
        StationResponse response4 = StationAcceptanceTest.지하철_역_등록되어_있음("신사역").as(StationResponse.class);

        lineThreeCreateRequest = new LineCreateRequest("3호선", "orange", response3.getId(), response4.getId(), 10);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineTwoCreateRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        지하철_노선_생성_요청(lineTwoCreateRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response2 = 지하철_노선_생성_요청(lineTwoCreateRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response2.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdLine1 = 지하철_노선_생성_요청(lineTwoCreateRequest);
        ExtractableResponse<Response> createdLine2 = 지하철_노선_생성_요청(lineThreeCreateRequest);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        assertThat(lineResponses.size()).isEqualTo(2);
        assertThat(lineResponses).contains(createdLine1.as(LineResponse.class), createdLine2.as(LineResponse.class));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = 지하철_노선_생성_요청(lineTwoCreateRequest).as(LineResponse.class);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + createdLineResponse.getId())
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_응답됨
        LineStationResponse lineStationResponse = response.as(LineStationResponse.class);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(lineStationResponse.getStations().size()).isEqualTo(2);
        assertThat(lineStationResponse.getStations().get(0).getName()).isEqualTo("강남역");
        assertThat(lineStationResponse.getStations().get(1).getName()).isEqualTo("삼성역");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = 지하철_노선_생성_요청(lineTwoCreateRequest).as(LineResponse.class);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(new LineUpdateRequest("3호선", "green"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .patch("/lines/" + createdLineResponse.getId())
                .then().log().all()
                .extract();
        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.jsonPath().getObject(".", LineResponse.class).getName()).isEqualTo("3호선");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = 지하철_노선_생성_요청(lineTwoCreateRequest).as(LineResponse.class);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + createdLineResponse.getId())
                .then().log().all()
                .extract();
        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static ExtractableResponse<Response> 지하철_노선_조회(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_등록되어_있음(LineCreateRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(LineCreateRequest request) {
        return RestAssured.given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }
}
