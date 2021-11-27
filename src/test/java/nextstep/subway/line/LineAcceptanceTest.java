package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        // given
        Map<String, String> params = 지하철_노선_더미_데이터_신분상선();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("lines", params).extract();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {

        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = 지하철_노선_더미_데이터_신분상선();
        지하철_노선_생성_요청("lines", params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("lines", params).extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> redLine = 지하철_노선_더미_데이터_신분상선();
        ExtractableResponse<Response> redLineResponse = 지하철_노선_생성_요청("lines", redLine).extract();

        Map<String, String> orangeLine = 지하철_노선_더미_데이터_삼호선();
        ExtractableResponse<Response> orangeLineResponse = 지하철_노선_생성_요청("lines", orangeLine).extract();

        // when
        // 지하철_노선_목록_조회_요청
        ValidatableResponse response = 지하철_노선_조회_요청("lines");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.extract().statusCode()).isEqualTo(HttpStatus.OK.value());
        response.body("lines.name", hasItems("신분당선", "3호선"));

        // 지하철_노선_목록_포함됨
        지하철_노선_목록_포함됨_검증(redLineResponse.header("Location"));
        지하철_노선_목록_포함됨_검증(orangeLineResponse.header("Location"));
    }

    private void 지하철_노선_목록_포함됨_검증(String location) {
        ExtractableResponse<Response> findLine1 = 지하철_노선_조회_요청(location).extract();
        assertThat(findLine1.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = 지하철_노선_더미_데이터_신분상선();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청("lines", params).extract();

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdResponse.header("Location")).extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> redLineParam = 지하철_노선_더미_데이터_신분상선();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청("lines", redLineParam).extract();

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청(createdResponse.header("Location")).extract();
        findResponse.response();

        Map<String, String> updateParams = 지하철_노선_더미_데이터_삼호선();
        ValidatableResponse updatedResponse = 지하철_노선_수정_요청(createdResponse.header("Location"), updateParams);

        // then
        // 지하철_노선_수정됨
        assertThat(updatedResponse.extract().statusCode()).isEqualTo(HttpStatus.OK.value());
        updatedResponse.body("id", is(1));
        updatedResponse.body("name", is("3호선"));
        updatedResponse.body("color", is("orange"));

    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = 지하철_노선_더미_데이터_신분상선();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청("lines", params).extract();

        // when
        // 지하철_노선_제거_요청
        지하철_노선_삭제_요청(createdResponse.header("Location"));

        // then
        // 지하철_노선_삭제됨
        ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청(createdResponse.header("Location")).extract();
        assertThat(findResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> 지하철_노선_더미_데이터_신분상선() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");
        return params;
    }

    private Map<String, String> 지하철_노선_더미_데이터_삼호선() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "3호선");
        params.put("color", "orange");
        return params;
    }

    private ValidatableResponse 지하철_노선_조회_요청(String url) {
        return requestSpecification(new HashMap<>())
                .get(url)
                .then().log().all();
    }

    private ValidatableResponse 지하철_노선_생성_요청(String url, Map<String, String> params) {
        return requestSpecification(params).when()
                .post(url)
                .then().log().all();
    }

    private ValidatableResponse 지하철_노선_수정_요청(String url, Map<String, String> params) {
        return requestSpecification(params).when()
                .put(url)
                .then().log().all();
    }

    private ValidatableResponse 지하철_노선_삭제_요청(String url) {
        return requestSpecification(new HashMap<>())
                .delete(url)
                .then().log().all();
    }

    private RequestSpecification requestSpecification(Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when();
    }
}
