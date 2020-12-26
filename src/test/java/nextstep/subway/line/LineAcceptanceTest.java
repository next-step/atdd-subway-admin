package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    @Autowired
    private LineRepository lineRepository;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_생성_요청
        response = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();

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
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createLine1 = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();
        assertThat(createLine1.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");

        ExtractableResponse<Response> createLine2 = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();
        assertThat(createLine2.statusCode()).isEqualTo(HttpStatus.CREATED.value());


        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> lineListResponse = RestAssured
                .given().log().all()
                .when().get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(lineListResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨

        List<Long> expectedLineIds = Arrays.asList(createLine1, createLine2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = lineListResponse.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds.size()).isNotEqualTo(0);
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() throws JSONException {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createLine1 = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();
        assertThat(createLine1.statusCode()).isEqualTo(HttpStatus.CREATED.value());



        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        HashMap responseMap = response.jsonPath().getJsonObject(".");

        JSONObject json =  new JSONObject(responseMap);
        assertThat(json.get("id")).isNotNull();
        System.out.println(json.get("id"));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() throws JSONException {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createLine1 = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();
        assertThat(createLine1.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_수정_요청
        Map<String, String> modifyParams = new HashMap<>();
        modifyParams.put("id", "1");
        modifyParams.put("name", "new_신분당선");
        modifyParams.put("color", "bg-red-600");
        ExtractableResponse<Response> patchResponse = RestAssured.given().log().all().
                body(modifyParams).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                patch("/lines").
                then().
                log().all().
                extract();
        assertThat(patchResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        // 지하철_노선_수정됨
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        HashMap responseMap = response.jsonPath().getJsonObject(".");

        JSONObject json =  new JSONObject(responseMap);
        String name = (String) json.get("name");
        assertThat(name.equalsIgnoreCase(modifyParams.get("name"))).isTrue();

    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "bg-red-600");

        ExtractableResponse<Response> createLine1 = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();
        assertThat(createLine1.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        // 지하철_노선_제거_요청

        ExtractableResponse<Response> deleteLineResponse = RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                delete("/lines/1").
                then().
                log().all().
                extract();
        assertThat(deleteLineResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());

        // then
        // 지하철_노선_삭제됨
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get("/lines/1")
                .then().log().all().extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        HashMap responseMap = response.jsonPath().getJsonObject(".");

        JSONObject json =  new JSONObject(responseMap);
        System.out.println(json);

    }
}
