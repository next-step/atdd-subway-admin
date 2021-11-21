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
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        final String name = "2호선";
        final String color = "red lighten-3";
        final long upStationId = 1;
        final long downStationId = 10;
        final double distance = 22.1;

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = createLine(name, color, upStationId, downStationId, distance);

        // then
        // 지하철_노선_생성됨
        assertCreateLine(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        final String name = "2호선";
        final String color = "red lighten-3";
        final long upStationId = 1;
        final long downStationId = 10;
        final double distance = 22.1;

        assertCreateLine(createLine(name, color, upStationId, downStationId, distance));

        // when
        // 지하철_노선_생성_요청
        final ExtractableResponse<Response> response = createLine(name, color, upStationId, downStationId, distance);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> createResponse1 = createLine("2호선", "red lighten-3", 1, 10, 22.1);
        assertCreateLine(createResponse1);

        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> createResponse2 = createLine("1호선", "red", 11, 20, 22.4);
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
        final List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
            .map(it -> extractId(getLocation(it)))
            .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath().getList("lineResponses", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        final String name = "2호선";
        final String color = "red lighten-3";
        final long upStationId = 1;
        final long downStationId = 10;
        final double distance = 22.1;

        final ExtractableResponse<Response> createResponse = createLine(
            name, color, upStationId, downStationId, distance
        );
        assertCreateLine(createResponse);

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
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        final String name = "2호선";
        final String color = "red lighten-3";
        final long upStationId = 1;
        final long downStationId = 10;
        final double distance = 22.1;

        final ExtractableResponse<Response> createResponse = createLine(
            name, color, upStationId, downStationId, distance
        );
        assertCreateLine(createResponse);

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
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_제거_요청

        // then
        // 지하철_노선_삭제됨
    }

    private ExtractableResponse<Response> createLine(final String name, final String color,
        final long upStationId, final long downStationId, final double distance) {
        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("name", name);
            put("color", color);
            put("upStationId", upStationId);
            put("downStationId", downStationId);
            put("distance", distance);
        }};

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
