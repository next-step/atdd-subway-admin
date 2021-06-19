package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceBefore.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final String RESOURCES = "/lines";
    private static final String PATH_FROM_HEADER = "LOCATION";

    @BeforeEach
    private void init() {
        super.setUp();
        //given (stations)
        requestCreateStation(1L, "강남역");
        requestCreateStation(2L, "역삼역");
        requestCreateStation(3L, "종로3가");
        requestCreateStation(4L, "청량리");
        requestCreateStation(5L, "홍제역");
        requestCreateStation(6L, "충정로");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given (강남역, 역삼역)

        // when
        // 지하철_노선_생성_요청
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = createLineAsTestCase(request);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(request.getName());
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(request.getColor());
        assertThat(response.body().jsonPath().getLong("stations[0].id")).isEqualTo(request.getUpStationId());
        assertThat(response.body().jsonPath().getString("stations[0].name")).isEqualTo("강남역");
        assertThat(response.body().jsonPath().getLong("stations[1].id")).isEqualTo(request.getDownStationId());
        assertThat(response.body().jsonPath().getString("stations[1].name")).isEqualTo("역삼역");
    }

    private ExtractableResponse<Response> createLineAsTestCase(LineRequest request) {
        return restAssured()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post(RESOURCES)
            .then()
            .log().all().extract();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest request = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);
        createLineAsTestCase(request);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLineAsTestCase(request);
        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(response.body().jsonPath().getString("message")).isEqualTo("이미 존재하는 Line 입니다");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        createLineAsTestCase(new LineRequest("1호선", "bg-blue-100", 3L, 4L, 10));
        // 지하철_노선_등록되어_있음
        createLineAsTestCase(new LineRequest("2호선", "bg-green-200", 1L, 2L, 10));
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = restAssured()
            .when().get(RESOURCES)
            .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        assertThat(response.body().jsonPath().getList("name")).contains("1호선");
        assertThat(response.body().jsonPath().getList("name")).contains("2호선");
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expected = createLineAsTestCase(
            new LineRequest("3호선", "bg-orange-100", 1L, 2L, 10));
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = restAssured()
            .when().get(expected.header(PATH_FROM_HEADER))
            .then().log().all().extract();
        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expected = createLineAsTestCase(
            new LineRequest("3호선", "bg-orange-100", 5L, 6L, 10));
        // when
        // 지하철_노선_수정_요청
        LineRequest updateRequest = new LineRequest("3호선", "bg-orange-300");
        ExtractableResponse<Response> response = restAssured()
            .body(updateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(expected.header(PATH_FROM_HEADER))
            .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정요청시 해당하는 노선이 없는 경우")
    @Test
    void updateLineNotExist() {
        // given
        //
        // when
        // DB에 없는 노선에 수정 요청
        LineRequest updateRequest = new LineRequest("3호선", "bg-orange-300", 5L, 6L, 10);

        ExtractableResponse<Response> response = restAssured()
            .body(updateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/1")
            .then().log().all().extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> expected = createLineAsTestCase(
            new LineRequest("3호선", "bg-orange-100", 5L, 6L, 10));
        // when
        // 지하철_노선_제거_요청
        // when
        ExtractableResponse<Response> response = restAssured()
            .when().delete(expected.header(PATH_FROM_HEADER))
            .then().log().all().extract();
        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("지하철 노선을 제거할때 해당하는 노선이 없는 경우")
    @Test
    void deleteLineNotExist() {
        // given
        //
        // when
        // DB에 없는 노선 삭제 요청
        ExtractableResponse<Response> response = restAssured()
            .when().delete("/lines/1")
            .then().log().all().extract();
        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
