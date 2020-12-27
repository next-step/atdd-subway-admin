package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
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

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
        assertThat(response.body()).isNotNull();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음("bg-red-600", "신분당선");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("bg-red-600", "신분당선");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void showLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음("bg-red-600", "신분당선");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음("bg-green-600", "2호선");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .when().get("/lines")
              .then().log().all()
              .extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expectedIdList = expectedIdList(createResponse1, createResponse2);
        List<Long> responseIdList = response.jsonPath().getList(".", LineResponse.class).stream()
              .map(it -> it.getId())
              .collect(Collectors.toList());
        assertThat(responseIdList).containsAll(expectedIdList);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_조회_요청

        // then
        // 지하철_노선_응답됨
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_수정_요청

        // then
        // 지하철_노선_수정됨
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

    private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String color, String name) {
        return 지하철_노선_생성_요청(color, name);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(String color, String name) {
        Map<String, String> params = new HashMap<>();
        params.put("color", color);
        params.put("name", name);

        return RestAssured.given().log().all()
              .contentType(MediaType.APPLICATION_JSON_VALUE)
              .body(params)
              .when().post("/lines")
              .then().log().all()
              .extract();
    }

    private List<Long> expectedIdList(ExtractableResponse<Response>... createResponses) {
        return Arrays.asList(createResponses).stream()
              .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
              .collect(Collectors.toList());
    }
}
