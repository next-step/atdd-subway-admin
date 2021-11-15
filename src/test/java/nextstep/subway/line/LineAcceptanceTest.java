package nextstep.subway.line;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final String NAME = "name";
    public static final String COLOR = "color";

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {

        Map<String, String> params = new HashMap<>();
        params.put(NAME, "1호선");
        params.put(COLOR, "남색");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured
            .given().log().all()
            .body(params)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .post("lines")
            .then().log().all()
            .extract();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        Map<String, String> params = new HashMap<>();
        params.put(NAME, "1호선");
        params.put(COLOR, "남색");

        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse = RestAssured
            .given()
            .log().all()
            .body(params)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .post("lines")
            .then()
            .log().all()
            .extract();

        // when
        ExtractableResponse<Response> response = RestAssured
            .given()
            .log().all()
            .body(params)
            .contentType(APPLICATION_JSON_VALUE)
            .when()
            .post("lines")
            .then()
            .log().all()
            .extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        save(new LineRequest("1호선","bg-red-600"));
        save(new LineRequest("2호선","bg-green-600"));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> findResponse = RestAssured
            .given()
            .contentType(APPLICATION_JSON_VALUE)
            .body(PageRequest.of(1,10))
            .when().get("lines")
            .then().log().all()
            .extract();

        // then
        assertThat(findResponse.statusCode()).isEqualTo(OK.value());
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

    private ExtractableResponse<Response> save(LineRequest lineOne) {
        ExtractableResponse<Response> createResponse = RestAssured
            .given().log().all()
            .body(lineOne)
            .contentType(APPLICATION_JSON_VALUE)
            .when().post("lines")
            .then().log().all()
            .extract();
        return createResponse;
    }
}
