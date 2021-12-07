package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        ExtractableResponse<Response> response = When.지하철_노선_생성_요청("신분당선", "bg-red-600");

        Then.지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        Given.지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        ExtractableResponse<Response> response = When.지하철_노선_생성_요청("신분당선", "bg-red-600");

        Then.지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        Given.지하철_노선_등록되어_있음("신분당선", "bg-red-600");
        Given.지하철_노선_등록되어_있음("2호선", "bg-green-600");

        ExtractableResponse<Response> response = When.지하철_노선_목록_조회_요청();

        Then.지하철_노선_목록_응답됨(response);
        Then.지하철_노선_목록_포함됨(response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        Given.지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        ExtractableResponse<Response> response = When.지하철_노선_조회_요청(1L);

        Then.지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        Given.지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        ExtractableResponse<Response> response = When.지하철_노선_수정_요청("구분당선", "bg-blue-600");

        Then.지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        Given.지하철_노선_등록되어_있음("신분당선", "bg-red-600");

        ExtractableResponse<Response> response = When.지하철_노선_제거_요청(1L);

        Then.지하철_노선_삭제됨(response);
    }

    static class Given {
        static void 지하철_노선_등록되어_있음(String name, String color) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("color", color);

            RestAssured.given()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(params)
                    .when()
                    .post("/lines");
        }
    }

    static class When {
        static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("color", color);

            return RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(params)
                    .when()
                    .post("/lines")
                    .then().log().all()
                    .extract();
        }

        static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
            return RestAssured.given().log().all()
                    .when()
                    .get("/lines")
                    .then().log().all()
                    .extract();
        }

        static ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
            return RestAssured.given().log().all()
                    .when()
                    .get("/lines/" + id)
                    .then().log().all()
                    .extract();
        }

        static ExtractableResponse<Response> 지하철_노선_수정_요청(String name, String color) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("color", color);

            return RestAssured.given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(params)
                    .when()
                    .put("/lines/1")
                    .then().log().all()
                    .extract();
        }

        static ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
            return RestAssured.given().log().all()
                    .when()
                    .delete("/lines/" + id)
                    .then().log().all()
                    .extract();
        }
    }

    static class Then {
        static void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        static void 지하철_노선_생성_실패됨(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        static void 지하철_노선_목록_응답됨(ExtractableResponse<Response> response) {
            ok(response);
        }

        static void 지하철_노선_목록_포함됨(ExtractableResponse<Response> response) {
            assertThat(response.jsonPath().getList(".")).hasSize(2);
        }

        static void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
            ok(response);
        }

        static void 지하철_노선_수정됨(ExtractableResponse<Response> response) {
            ok(response);
        }

        static void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        }

        private static void ok(ExtractableResponse<Response> response) {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        }
    }
}
