package nextstep.subway.line;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = createLine01();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        createLine01();

        // when
        ExtractableResponse<Response> response = createLine01();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse01 = createLine01();
        ExtractableResponse<Response> createResponse02 = createLine02();

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines")
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Stream.of(createResponse01, createResponse02)
            .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
            .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine01();

        // when
        String expectLineId = createResponse.header("Location").split("/")[2];
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .get("/lines/" + expectLineId)
            .then().log().all()
            .extract();
        String actualLineId = response.jsonPath().get("id").toString();

        // then
        assertEquals(expectLineId, actualLineId);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine01();

        // when
        String updateLineId = createResponse.header("Location").split("/")[2];
        Map<String, String> params = new HashMap<>();
        params.put("name", "1호선");
        params.put("color", "#FF0000");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .put("/lines/" + updateLineId)
            .then().log().all()
            .extract();

        // then
        assertEquals(params.get("color"), response.jsonPath().get("color"));
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine01();

        // when
        String uri = createResponse.header("Location");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
            .when()
            .delete(uri)
            .then().log().all()
            .extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> createLine01() {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "1호선");
                      put("color", "#0000FF");
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();
    }

    private ExtractableResponse<Response> createLine02() {
        return RestAssured.given().log().all()
            .body(new HashMap<String, String>() {{
                      put("name", "2호선");
                      put("color", "#008000");
                  }}
            )
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post("/lines")
            .then().log().all().extract();
    }
}
