package nextstep.subway.Line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
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
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {
    @LocalServerPort
    int port;
    Long upStationId;
    Long downStationId;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("노선을 생성한다.")
    @Test
    void createLineTest() {
        // when
        ExtractableResponse<Response> response = createLine("신분당선", "bg-red-600", "광교역", "신사역", 10L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        ExtractableResponse<Response> lineResponse = selectLine(response.as(LineResponse.class).getId());
        assertThat(lineResponse.as(LineResponse.class).getName()).isEqualTo("신분당선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        createLine("신분당선", "bg-red-600", "광교역", "신사역", 10L);
        createLine("2호선", "bg-green-600", "까치산", "신도림", 20L);

        // when
        ExtractableResponse<Response> response = selectAllLine();
        List<LineResponse> lines = response.jsonPath().getList(".", LineResponse.class);

        // then
        assertAll(
                () -> assertThat(lines).hasSize(2),
                () -> assertThat(lines.stream().map(lineResponse -> lineResponse.getName()).collect(Collectors.toList())).contains("신분당선"),
                () -> assertThat(lines.stream().map(lineResponse -> lineResponse.getName()).collect(Collectors.toList())).contains("2호선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = createLine("신분당선", "bg-red-600", "광교역", "신사역", 10L);

        // when
        ExtractableResponse<Response> response = selectLine(createResponse.as(LineResponse.class).getId());
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        LineResponse line = response.as(LineResponse.class);
        assertAll(
                () -> assertThat(line.getName()).isEqualTo("신분당선"),
                () -> assertThat(line.getColor()).isEqualTo("bg-red-600")
        );
    }

    public ExtractableResponse<Response> createLine(String name, String color, String upStation, String downStation, Long distance) {
        Map<String, String> params = createParams(name, color, upStation, downStation, distance);
        ExtractableResponse<Response> response =
                RestAssured.given().log().all()
                        .body(params)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .when().post("/lines")
                        .then().log().all()
                        .extract();

        return response;
    }

    public Map<String, String> createParams(String name, String color, String upStation, String downStation, Long distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(StationAcceptanceTest.createStation(upStation)));
        params.put("downStationId", String.valueOf(StationAcceptanceTest.createStation(downStation)));
        params.put("distance", "10");

        return params;
    }

    public ExtractableResponse<Response> selectAllLine() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> selectLine(Long id) {
        return  RestAssured.given().log().all()
                .when().get("/lines/" + String.valueOf(id))
                .then().log().all()
                .extract();
    }
}
