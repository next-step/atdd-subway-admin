package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private LineRequest request;

    @BeforeEach
    void setRequest() {
        StationResponse upStation  = 지하철역_생성_요청("강남역");
        StationResponse downStation = 지하철역_생성_요청("광교역");
        request = new LineRequest("신분당선", "br-red-600", upStation.getId(), downStation.getId(), 10);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        // then
        지하철노선_응답됨(response, HttpStatus.CREATED);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);

        // then
        지하철노선_응답됨(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 전체 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        지하철노선_생성_요청(request);

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회_요청();

        // then
        지하철노선_응답됨(response, HttpStatus.OK);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청(request);
        String url = createResponse.header("Location");

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(url);

        // then
        지하철노선_응답됨(getResponse, HttpStatus.OK);
        LineResponse response = getResponse.as(LineResponse.class);
        assertThat(response).isNotNull();
    }

    public static ExtractableResponse<Response> 지하철노선_조회_요청(String url) {
        return RestAssured
                .given().log().all()
                .when().get(url)
                .then().log().all().extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청(request);
        String url = createResponse.header("Location");

        // when
        LineRequest updateRequest = new LineRequest("3호선", "black");
        ExtractableResponse<Response> response = 지하철노선_수정_요청(url, updateRequest);

        // then
        지하철노선_응답됨(response, HttpStatus.OK);
        지하철노선_수정_검증됨(url, updateRequest);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청(request);
        String url = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = 지하철노선_제거_요청(url);

        // then
        지하철노선_응답됨(response, HttpStatus.NO_CONTENT);
        지하철노선_삭제_검증됨(url);
    }

    @DisplayName("노선생성시 종점역 상행, 하행을 함께 추가")
    @Test
    void createWithUpStationAndDownStation() {
        ExtractableResponse<Response> response = 지하철노선_생성_요청(request);
        지하철노선_응답됨(response, HttpStatus.CREATED);
    }


    private StationResponse 지하철역_생성_요청(String name) {
        ExtractableResponse<Response> stationResponse = StationAcceptanceTest.지하철역_생성_요청(name);
        return stationResponse.body().as(StationResponse.class);
    }

    public static ExtractableResponse<Response> 지하철노선_생성_요청(LineRequest request) {
        return RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private void 지하철노선_응답됨(ExtractableResponse<Response> response, HttpStatus badRequest) {
        Assertions.assertThat(response.statusCode()).isEqualTo(badRequest.value());
    }

    public static ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(String url, LineRequest updateRequest) {
        return RestAssured
                .given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(url)
                .then().log().all().extract();
    }

    private void 지하철노선_수정_검증됨(String url, LineRequest updateRequest) {
        ExtractableResponse<Response> response = 지하철노선_조회_요청(url);
        LineResponse lineResponse = response.body().as(LineResponse.class);
        assertAll(
                () -> assertThat(lineResponse.getName()).isEqualTo(updateRequest.getName()),
                () -> assertThat(lineResponse.getColor()).isEqualTo(updateRequest.getColor())
        );
    }

    private ExtractableResponse<Response> 지하철노선_제거_요청(String url) {
        return RestAssured
                .given().log().all()
                .when().delete(url)
                .then().log().all().extract();
    }

    private void 지하철노선_삭제_검증됨(String url) {
        ExtractableResponse<Response> response = 지하철노선_조회_요청(url);
        지하철노선_응답됨(response, HttpStatus.BAD_REQUEST);
    }
}
