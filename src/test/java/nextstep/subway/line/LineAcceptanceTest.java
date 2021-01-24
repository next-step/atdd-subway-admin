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
import org.junit.jupiter.api.Assertions;
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
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청("신분당선", "red");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("신분당선", "red");

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());

    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        List<LineResponse> lineResponses = Arrays.asList(지하철_노선_응답_객체_추출(지하철_노선_생성_요청("신분당선", "red")),
            지하철_노선_응답_객체_추출(지하철_노선_생성_요청("2호선", "green")));
        List<Long> expectedIds = 지하철_노선_응답_객체_아이디_추출(lineResponses);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        Assertions.assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            List<Long> resultIds = Arrays.stream(response.as(LineResponse[].class))
                .map(LineResponse::getId)
                .collect(Collectors.toList());

            assertThat(resultIds)
                .containsAll(expectedIds);
        });

    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            get("/lines").
            then().
            log().all().
            extract();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청("신분당선", "red");
        LineResponse createdLineResponse = 지하철_노선_응답_객체_추출(createdResponse);
        String locationHeader = 지하철_노선_생성_요청_LOCATION_헤더_추출(createdResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            get(locationHeader).
            then().
            log().all().
            extract();

        // then
        // 지하철_노선_응답됨

        Assertions.assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

            assertThat(response.as(LineResponse.class).getId())
                .isEqualTo(createdLineResponse.getId());
        });
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

    private List<Long> 지하철_노선_응답_객체_아이디_추출(List<LineResponse> lineResponses) {
        return lineResponses.stream().map(LineResponse::getId).collect(Collectors.toList());
    }

    private LineResponse 지하철_노선_응답_객체_추출(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class);
    }


    private String 지하철_노선_생성_요청_LOCATION_헤더_추출(ExtractableResponse<Response> response) {
        return response.header("Location");
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return 지하철_노선_생성_요청(params);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, Object> params) {
        return RestAssured.given().log().all().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            extract();
    }
}
