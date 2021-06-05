package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.BeforeEach;
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
    private String name;
    private String color;

    @BeforeEach
    public void setup() {
        name = "4호선";
        color = "blue";
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        ExtractableResponse<Response> response = 지하철_노선_생성(name, color);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        지하철_노선_생성(name, color);
        ExtractableResponse<Response> response = 지하철_노선_생성(name, color);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        String name2 = "2호선";
        String color2 = "green";

        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성(name, color);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성(name2, color2);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
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
        ExtractableResponse<Response> createResponse = 지하철_노선_생성(name, color);
        long createLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        ExtractableResponse<Response> response = 지하철_노선_조회(createLineId);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("id")).isEqualTo("1");
    }

    @DisplayName("지하철 노선을 조회 실패")
    @Test
    void getLineFail() {
        ExtractableResponse<Response> response = 지하철_노선_조회(1L);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        String changeName = "2호선";
        String changeColor = "green";

        ExtractableResponse<Response> createResponse = 지하철_노선_생성(name, color);
        long createLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        Map<String, String> line = new HashMap<>();
        line.put("name", changeName);
        line.put("color", changeColor);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(line)
                .when()
                .put("/lines/" + createLineId)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.body().jsonPath().getString("name")).isEqualTo(changeName);
        assertThat(response.body().jsonPath().getString("color")).isEqualTo(changeColor);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        ExtractableResponse<Response> createResponse = 지하철_노선_생성(name, color);
        long createLineId = Long.parseLong(createResponse.header("Location").split("/")[2]);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/lines/" + createLineId)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(지하철_노선_조회(createLineId).statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성(String name, String color) {
        Map<String, String> line = new HashMap<>();
        line.put("name", name);
        line.put("color", color);

        return RestAssured.given().log().all()
                .body(line)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(long createLineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/lines/" + createLineId)
                .then().log().all()
                .extract();
    }
}
