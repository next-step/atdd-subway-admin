package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = doPost("/lines", new LineRequest("2호선", "초록"));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest requestBody = new LineRequest("2호선", "초록");
        doPost("/lines", requestBody);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = doPost("/lines", requestBody);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = doPost("/lines", new LineRequest("2호선", "초록"));
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse2 = doPost("/lines", new LineRequest("1호선", "파랑"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = doGet("/lines");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<Long> expected = Stream.of(createResponse, createResponse2)
                .map(it -> it.header("Location"))
                .map(this::pathVariableToLong)
                .collect(Collectors.toList());

        List<Long> result = response.jsonPath()
                .getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(expected).containsAll(result);
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

    private ExtractableResponse<Response> doPost(String url, Object body) {
        return RestAssured
                .given().log().all()
                .body(body)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(url)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> doGet(String url) {
        return doGetWithParameter(url, null);
    }


    private ExtractableResponse<Response> doGetWithParameter(String url, Object body) {
        RequestSpecification specification = RestAssured.given().log().all();
        if (body != null) {
            specification = specification.body(body)
                    .contentType(MediaType.APPLICATION_JSON_VALUE);
        }
        return specification
                .when()
                .get(url)
                .then().log().all()
                .extract();
    }

    private Long pathVariableToLong(String location) {
        String regex = "/lines/(\\d)$";
        Matcher m = Pattern.compile(regex).matcher(location);
        if (m.find()) {
            return Long.parseLong(m.group(1));
        }
        throw new RuntimeException("Not matched expression : " + regex);
    }
}
