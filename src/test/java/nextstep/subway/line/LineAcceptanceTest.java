package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다. 노선을 생성할떄 종점역(상행,하행)정보를 함께 생성해한다.")
    @Test
    void createLine() {
        //given
        StationResponse 상행 = 지하철_역_생성_요청(강남역_생성_파라미터()).as(StationResponse.class);
        StationResponse 하행 = 지하철_역_생성_요청(역삼역_생성_파라미터()).as(StationResponse.class);
        LineRequest lineRequest = 신분당선_생성_파라미터(상행, 하행);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationResponse 상행 = 지하철_역_생성_요청(강남역_생성_파라미터()).as(StationResponse.class);
        StationResponse 하행 = 지하철_역_생성_요청(역삼역_생성_파라미터()).as(StationResponse.class);
        지하철_노선_생성_요청(신분당선_생성_파라미터(상행, 하행));

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_생성_파라미터(상행, 하행));

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse 상행 = 지하철_역_생성_요청(강남역_생성_파라미터()).as(StationResponse.class);
        StationResponse 하행 = 지하철_역_생성_요청(역삼역_생성_파라미터()).as(StationResponse.class);
        ExtractableResponse<Response> 신분당선 = 지하철_노선_생성_요청(신분당선_생성_파라미터(상행, 하행));
        ExtractableResponse<Response> 이호선 = 지하철_노선_생성_요청(이호선_생성_파라미터(상행, 하행));
        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, 신분당선, 이호선);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse 상행 = 지하철_역_생성_요청(강남역_생성_파라미터()).as(StationResponse.class);
        StationResponse 하행 = 지하철_역_생성_요청(역삼역_생성_파라미터()).as(StationResponse.class);
        LineResponse 신분당선 = 지하철_노선_생성_요청(신분당선_생성_파라미터(상행, 하행)).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선.getId());

        // then
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse 상행 = 지하철_역_생성_요청(강남역_생성_파라미터()).as(StationResponse.class);
        StationResponse 하행 = 지하철_역_생성_요청(역삼역_생성_파라미터()).as(StationResponse.class);
        LineResponse 신분당선 = 지하철_노선_생성_요청(신분당선_생성_파라미터(상행, 하행)).as(LineResponse.class);

        // when
        Map<String, String> updateParams = new HashMap<>();
        updateParams.put("name", "신분당선");
        updateParams.put("color", "bg-deepRed-600");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(신분당선.getId(), 신분당선_수정_파라미터(상행, 하행));

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse 상행 = 지하철_역_생성_요청(강남역_생성_파라미터()).as(StationResponse.class);
        StationResponse 하행 = 지하철_역_생성_요청(역삼역_생성_파라미터()).as(StationResponse.class);
        LineResponse 신분당선 = 지하철_노선_생성_요청(신분당선_생성_파라미터(상행, 하행)).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선.getId());

        // then
        지하철_노선_삭제됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long lineId, LineRequest updateRequest) {
        return RestAssured.given().log().all()
                .body(updateRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long lineId) {
        return RestAssured.given().log().all()
                .when()
                .delete("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    private void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isNotNull();
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, ExtractableResponse<Response>... createResponse) {
        List<Long> expectedLineIds = Arrays.asList(createResponse).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private LineRequest 신분당선_생성_파라미터(StationResponse upStationResponse, StationResponse downStationResponse) {
        return new LineRequest("신분당선", "bg-red-600", upStationResponse.getId(), downStationResponse.getId(), 10);
    }

    private LineRequest 이호선_생성_파라미터(StationResponse upStationResponse, StationResponse downStationResponse) {
        return new LineRequest("2호선", "bg-green-600", upStationResponse.getId(), downStationResponse.getId(), 10);
    }

    private LineRequest 신분당선_수정_파라미터(StationResponse upStationResponse, StationResponse downStationResponse) {
        return new LineRequest("신분당선", "bg-deepRed-600", upStationResponse.getId(), downStationResponse.getId(), 10);
    }
}

