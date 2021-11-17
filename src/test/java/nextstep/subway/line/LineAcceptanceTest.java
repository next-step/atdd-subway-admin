package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "blue");
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "blue");
        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "blue");
        ExtractableResponse<Response> postResponse1 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        params.put("name", "2호선");
        params.put("color", "green");
        ExtractableResponse<Response> postResponse2 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        // when
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .when()
                .get("lines")
                .then().log().all()
                .extract();

        // then
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> expectedIds = Arrays.asList(postResponse1, postResponse2).stream()
                .map(resp -> Long.parseLong(resp.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultIds = getResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(expectedIds).containsAll(resultIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "blue");
        ExtractableResponse<Response> postResponse1 = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("lines")
                .then().log().all()
                .extract();

        Long insertedId = Long.parseLong(postResponse1.header("Location").split("/")[2]);
        // when
        ExtractableResponse<Response> getResponse = RestAssured.given().log().all()
                .when().get("line/{id}", insertedId)
                .then().log().all()
                .extract();

        // then
        LineResponse resultLine = getResponse.jsonPath().getObject("", LineResponse.class);
        assertThat(resultLine).isNotNull();
        assertThat(resultLine.getId()).isEqualTo(insertedId);
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
