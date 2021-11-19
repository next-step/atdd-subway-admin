package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선 생성")
    @Nested
    class CreateLineTest {

        @DisplayName("지하철 노선을 생성한다.")
        @Test
        void createLine() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("박달-강남선", "blue");

            // then
            지하철_노선이_생성된다(response);
        }

        @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
        @Test
        void givenDuplicateLineNameThenFail() {
            // given
            지하철_노선_등록되어_있음("박달-강남선", "blue");

            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("박달-강남선", "blue");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        @DisplayName("공백의 노선명으로 지하철 노선을 생성한다.")
        @Test
        void givenEmptyNameThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("", "blue");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        @DisplayName("공백의 노선색상으로 지하철 노선을 생성한다.")
        @Test
        void givenEmptyColorThenFail() {
            // when
            ExtractableResponse<Response> response = 지하철_노선_생성_요청("황금노선현", "");

            // then
            지하철_노선_생성이_실패한다(response);
        }

        private ExtractableResponse<Response> 지하철_노선_등록되어_있음(String name, String color) {
            return 지하철_노선_생성_요청(name, color);
        }

        private void 지하철_노선이_생성된다(ExtractableResponse<Response> response) {
            Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        }

        private void 지하철_노선_생성이_실패한다(ExtractableResponse<Response> response) {
            Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        }

        private ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color) {
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("color", color);

            return RestAssured
                    .given().log().all()
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(params)
                    .when().post("/lines")
                    .then().log().all().extract();
        }
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음

        // when
        // 지하철_노선_목록_조회_요청

        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
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
}
