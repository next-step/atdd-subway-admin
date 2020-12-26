package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String DEFAULT_LINES_URI = "/lines";
    private static final String DEFAULT_URI_GET_PARAMETER = "";
    final Map<String, String> params = new HashMap<>();

    @BeforeEach
    void 노선_생성_요청_파라미터_설정() {
        노선_생성_요청시_이름_색상_설정("2호선", "초록색");
    }

    void 노선_생성_요청시_이름_색상_설정(final String name, final String color) {
        params.put("name", name);
        params.put("color", color);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.header("Location")).isBlank();
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청() {
        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(DEFAULT_LINES_URI)
            .then().log().all()
            .extract();
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        final ExtractableResponse<Response> firstCreateResponse = 지하철_노선_생성_요청();
        // 지하철_노선_등록되어_있음
        노선_생성_요청시_이름_색상_설정("까치산역", "보라색");
        final ExtractableResponse<Response> secondCreateResponse = 지하철_노선_생성_요청();

        // when
        // 지하철_노선_목록_조회_요청
        final ExtractableResponse<Response> response = getRequest(DEFAULT_URI_GET_PARAMETER);

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        final List<Long> expectedLineIds = Stream.of(firstCreateResponse, secondCreateResponse)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        final List<Long> resultLineIds = response.jsonPath()
            .getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        final LineResponse createdLine = 지하철_노선_생성_요청().as(LineResponse.class);

        // when
        // 지하철_노선_조회_요청
        final ExtractableResponse<Response> response = getRequest("/" + createdLine.getId());

        final LineResponse getLine = response.as(LineResponse.class);

        // then
        // 지하철_노선_응답됨
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(createdLine.getId()).isEqualTo(getLine.getId()),
            () -> assertThat(createdLine.getName()).isEqualTo(getLine.getName()),
            () -> assertThat(createdLine.getColor()).isEqualTo(getLine.getColor())
        );
    }

    @DisplayName("존재 하지 않는 지하철 노선을 조회한다.")
    @Test
    void getNotExistLine() {
        // when
        // 지하철_노선_조회_요청
        final ExtractableResponse<Response> response = getRequest("/1");

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private ExtractableResponse<Response> getRequest(final String url) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get(DEFAULT_LINES_URI + url)
            .then().log().all()
            .extract();
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
}
