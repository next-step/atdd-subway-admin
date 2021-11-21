package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    public static ExtractableResponse<Response> createResponse;

    public static final Map<String, Object> CREATE_LINE_FIXTURE = new HashMap<String, Object>() {{
        put("name", "2호선");
        put("color", "red lighten-3");
        put("upStationId", 1);
        put("downStationId", 10);
        put("distance", 22.1);
    }};

    @TestFactory
    Stream<DynamicTest> 지하철_노선_시나리오() {
        return Stream.of(
            DynamicTest.dynamicTest("지하철 노선을 생성한다.", this::지하철_노선을_생성한다),
            DynamicTest.dynamicTest("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.", this::기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다),
            DynamicTest.dynamicTest("지하철 노선 목록을 조회한다.", this::지하철_노선_목록을_조회한다),
            DynamicTest.dynamicTest("지하철 노선을 조회한다.", this::지하철_노선을_조회한다),
            DynamicTest.dynamicTest("지하철 노선을 수정한다.", this::지하철_노선을_수정한다),
            DynamicTest.dynamicTest("지하철 노선을 제거한다.", this::지하철_노선을_제거한다)
        );
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선을_생성한다() {
        // when
        // 지하철_노선_생성_요청
        createResponse = createLine(CREATE_LINE_FIXTURE);

        // then
        // 지하철_노선_생성됨
        assertCreateLine(createResponse);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = createLine(CREATE_LINE_FIXTURE);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 지하철_노선_목록을_조회한다() {
        // given
        // 지하철_노선_등록되어_있음
        final HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("name", "1호선");
            put("color", "red");
            put("upStationId", 11);
            put("downStationId", 20);
            put("distance", 22.4);
        }};
        final ExtractableResponse<Response> createResponse2 = createLine(params);
        assertCreateLine(createResponse2);

        // when
        // 지하철_노선_목록_조회_요청
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

        // 지하철_노선_목록_포함됨
        final List<Long> expectedLineIds = Stream.of(createResponse, createResponse2)
            .map(it -> extractId(getLocation(it)))
            .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList("lineResponses", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선을_조회한다() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_조회_요청
        final String uri = getLocation(createResponse);

        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK);

        final long expectedLineId = extractId(uri);
        final long resultLineId = response.jsonPath().getObject(".", LineResponse.class).getId();

        assertThat(resultLineId).isEqualTo(expectedLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 지하철_노선을_수정한다() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청
        final String updateName = "3호선";
        final String updateColor = "red";
        final HashMap<String, Object> params = new HashMap<String, Object>() {{
            put("name", updateName);
            put("color", updateColor);
        }};

        final String uri = getLocation(createResponse);
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_수정됨
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_OK),
            () -> assertThat(response.body().jsonPath().getObject(".", LineResponse.class))
                .extracting(LineResponse::getName, LineResponse::getColor)
                .containsExactly(updateName, updateColor)
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void 지하철_노선을_제거한다() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청
        final String uri = getLocation(createResponse);
        final ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_NO_CONTENT);
    }

    private ExtractableResponse<Response> createLine(final Map<String, Object> params) {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }

    private void assertCreateLine(ExtractableResponse<Response> response) {
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_CREATED),
            () -> assertThat(getLocation(response)).isNotBlank()
        );
    }

    private long extractId(String location) {
        return Long.parseLong(location.split("/")[2]);
    }

    private String getLocation(ExtractableResponse<Response> response) {
        return response.header("Location");
    }
}
