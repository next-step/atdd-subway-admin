package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.mapper.TypeRef;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = 지하철_역_생성_요청(new StationRequest("강남역"))
            .jsonPath().getLong("id");
        downStationId = 지하철_역_생성_요청(new StationRequest("방배역"))
            .jsonPath().getLong("id");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(new LineRequest(
            "2호선", "bg-green", upStationId, downStationId, 10));

        // then
        // 지하철_노선_생성됨
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청(new LineRequest(
            "2호선", "bg-green", upStationId, downStationId, 10));

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(new LineRequest(
            "2호선", "bg-green", upStationId, downStationId, 10));

        // then
        // 지하철_노선_생성_실패됨
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        Long lineFourUpStationId = 지하철_역_생성_요청(new StationRequest("명동역"))
            .jsonPath().getLong("id");
        Long lineFourDownStationId = 지하철_역_생성_요청(new StationRequest("사당역"))
            .jsonPath().getLong("id");

        Long lineTwoId = 지하철_노선_생성_요청(new LineRequest(
            "2호선", "bg-green", upStationId, downStationId, 10
        )).jsonPath().getLong("id");
        Long lineFourId = 지하철_노선_생성_요청(new LineRequest(
            "4호선", "bg-blue", lineFourUpStationId, lineFourDownStationId, 10
        )).jsonPath().getLong("id");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        지하철_노선_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(lineTwoId, lineFourId));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long savedLineId = 지하철_노선_생성_요청(new LineRequest(
            "2호선", "bg-green", upStationId, downStationId, 10))
            .jsonPath().getLong("id");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(savedLineId);

        // then
        // 지하철_노선_응답됨
        지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long lineTwoId = 지하철_노선_생성_요청(new LineRequest(
            "2호선", "bg-green", upStationId, downStationId, 10))
            .jsonPath().getLong("id");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(
            lineTwoId, new LineRequest("4호선", "bg-blue"));

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Long lineTwoId = 지하철_노선_생성_요청(new LineRequest(
            "2호선", "bg-green", upStationId, downStationId, 10))
            .jsonPath().getLong("id");

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineTwoId);

        // then
        // 지하철_노선_삭제됨
        지하철_노선_삭제됨(response);
    }

    static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then()
            .log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long lineId) {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/{id}", lineId)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long lineTwoId, LineRequest params) {
        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/{id}", lineTwoId)
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long lineId) {
        return RestAssured
            .given().log().all()
            .when()
            .delete("/lines/{id}", lineId)
            .then().log().all().extract();
    }

    private void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response, List<Long> lineIds) {
        List<LineResponse> lines = response.as(new TypeRef<List<LineResponse>>() {
        });
        assertThat(lines).hasSize(2);
        assertThat(lines).extracting("id")
            .containsAll(lineIds);
    }

    private void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
