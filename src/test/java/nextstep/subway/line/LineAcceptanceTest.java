package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 인수테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 노선을 생성을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("새로운 노선을 생성한다.")
    @Test
    void createLine() {
        //when
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", "#EEEEEE");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> response_read =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().get("/lines")
                        .then().log().all()
                        .extract();

        //then
        List<String> lineNames = response_read.jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf("7호선");
    }

    /**
     * Given 노선을 생성하고
     * When 기존에 존재하는 노선 이름으로 노선을 생성하면
     * Then 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 노선 이름으로 새로운 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", "#EEEEEE");

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        //when
        params.clear();
        params.put("name", "7호선");
        params.put("color", "#FFFFFF");

        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * When 색상이 없이 노선을 생성하면
     * Then 노선 생성이 안된다
     */
    @DisplayName("Color를 지정하지 않고 노선을 생성한다.")
    @Test
    void createLineWithOutColor() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    /**
     * Given 2개의 노선을 생성하고
     * When 노선 목록을 조회하면
     * Then 2개의 노선을 응답 받는다
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLines() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", "#EEEEEE");

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        params.clear();
        params.put("name", "8호선");
        params.put("color", "#FFFFFF");

        RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        //when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();

        //then
        List<String> lines = response.jsonPath().getList("name", String.class);
        assertThat(lines).contains("7호선", "8호선");
    }

    /**
     * Given 노선을 생성하고
     * When 그 노선을 삭제하면
     * Then 그 노선 목록 조회 시 생성한 노선을 찾을 수 없다
     */
    @DisplayName("새로운 노선을 생성하고 제거한다.")
    @Test
    void deleteLine() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "7호선");
        params.put("color", "#EEEEEE");

        ExtractableResponse<Response> response_create = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();

        LineResponse lineResponse = response_create.as(LineResponse.class);

        //when
        ExtractableResponse<Response> response_delete = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + lineResponse.getId())
                .then().log().all()
                .extract();

        assertThat(response_delete.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
