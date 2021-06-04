package nextstep.subway.line;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LinesSubResponse;
import nextstep.subway.utils.DatabaseCleanup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest extends AcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanup databaseCleanup;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        databaseCleanup.execute();
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성("신분당선", "red");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        //given
        ExtractableResponse<Response> givenResponse = 지하철_노선_생성("신분당선", "red");
        assertThat(givenResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성("신분당선", "red");

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CONFLICT.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() throws JsonProcessingException {
        //given
        ExtractableResponse<Response> givenResponse1 = 지하철_노선_생성("신분당선", "red");
        assertThat(givenResponse1.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        ExtractableResponse<Response> givenResponse2 = 지하철_노선_생성("분당선", "yellow");
        assertThat(givenResponse2.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get("lines")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        List<Long> list = response.jsonPath().getList("$[*].id");

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.contains(givenResponse1.jsonPath().get("$.id"))).isTrue();
        assertThat(list.contains(givenResponse2.jsonPath().get("$.id"))).isTrue();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() throws JsonProcessingException {
        //given
        ExtractableResponse<Response> givenResponse = 지하철_노선_생성("신분당선", "red");
        LineResponse givenLine = givenResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(givenResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        Map<String, String> params = new HashMap<>();
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId", givenLine.getId())
                .when().get("/lines/{lineId}")
                .then().log().all().extract();
        LinesSubResponse linesSubResponse = response.jsonPath().getObject(".", LinesSubResponse.class);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(linesSubResponse.getName()).isEqualTo("신분당선");
        assertThat(linesSubResponse.getColor()).isEqualTo("red");
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> givenResponse = 지하철_노선_생성("신분당선", "red");
        assertThat(givenResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "구분당선");
        params.put("color", "blue");
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("lineId", givenResponse.jsonPath().get("$.id").toString())
                .when().put("lines/{lineId}")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> givenResponse = 지하철_노선_생성("신분당선", "red");
        assertThat(givenResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .pathParam("lineId", givenResponse.jsonPath().get("$.id").toString())
                .when().delete("lines/{lineId}")
                .then().log().all().extract();

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String name, String color) {
        Map<String, String> params = 지하철_노선_생성_파라미터(name, color);
        return 지하철_노선_생성_요청(params);
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
        return response;
    }

    private Map<String, String> 지하철_노선_생성_파라미터(String name, String color) {
        Map<String, String> givenParams = new HashMap<>();
        givenParams.put("name", name);
        givenParams.put("color", color);
        return givenParams;
    }
}
