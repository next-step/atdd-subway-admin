package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name","1호선");
        params.put("color","파란색");
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();
        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Content-Type")).isEqualTo("application/json");
        assertAll(
                () -> assertThat(response.jsonPath().getInt("id")).isEqualTo(1),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("1호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("파란색"),
                () -> assertThat(response.jsonPath().getString("createdDate")).isNotNull(),
                () -> assertThat(response.jsonPath().getString("modifiedDate")).isNotNull()
        );
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name","1호선");
        params.put("color","파란색");
        // when
        // 지하철_노선_생성_요청
        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name","1호선");
        params.put("color","파란색");

        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();
        // 지하철_노선_등록되어_있음
        params.clear();
        params.put("name","2호선");
        params.put("color","초록색");
        RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all().extract();
        // when
        // 지하철_노선_목록_조회_요청
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when()
                .get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        response.jsonPath().getList(".", LineResponse.class)
                .forEach(line -> {
                    MatcherAssert.assertThat(line.getId(),
                            anyOf(equalTo(1L), equalTo(2L)));
                    MatcherAssert.assertThat(line.getName(),
                            either(containsString("1호선")).or(containsString("2호선")));
                    MatcherAssert.assertThat(line.getColor(),
                            either(containsString("파란색")).or(containsString("초록색")));
                    assertThat(line.getCreatedDate()).isNotNull();
                    assertThat(line.getModifiedDate()).isNotNull();
                });
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
