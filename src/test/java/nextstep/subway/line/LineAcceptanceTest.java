package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLine("2호선", "green");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotNull();
        assertThat(response.header("Location")).contains("/lines/");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        createLine("2호선", "green");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createLine("2호선", "green");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> firstLine = createLine("1호선", "navy");
        ExtractableResponse<Response> secondLine = createLine("2호선", "green");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = getLine("");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expectIds = Arrays.asList(firstLine, secondLine).stream()
            .map(this::getIdWithResponse)
            .collect(Collectors.toList());
        List<Long> resultIds = getIdsWithResponse(response);
        assertThat(checkIdsEquals(expectIds, resultIds)).isTrue();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        createLine("2호선", "green");

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = getLine("/1");

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLine2() {
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = getLine("/1");

        // then
        // 지하철_노선_응답_실패
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdLine = createLine("2호선", "green");

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateLine("1호선", "navy",
            getIdWithResponse(createdLine));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
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

    private ExtractableResponse<Response> createLine(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all()
            .extract();
    }


    private ExtractableResponse<Response> getLine(String path) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines" + path)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> updateLine(String name, String color, Long id) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + id)
            .then().log().all()
            .extract();
    }

    private List<Long> getIdsWithResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getList(".", LineResponse.class).stream()
            .map(l -> l.getId())
            .collect(Collectors.toList());
    }

    private Long getIdWithResponse(ExtractableResponse<Response> response) {
        return response.jsonPath().getObject(".", LineResponse.class).getId();
    }

    private boolean checkIdsEquals(List<Long> expected, List<Long> result) {
        for (int i = 0; i < expected.size(); i++) {
            if (expected.get(i) != result.get(i)) {
                return false;
            }
        }
        return true;
    }

}
