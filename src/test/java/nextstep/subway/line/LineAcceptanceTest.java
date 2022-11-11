package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
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
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 기능")
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
     * When 지하철노선을 생성하면
     * Then 지하철노선이 생성된다
     * Then 지하철노선 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        long upStationId = createStation("강남역").jsonPath().getLong("id");
        long downStationId = createStation("논현역").jsonPath().getLong("id");
        ExtractableResponse<Response> response = createLine(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build());

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<Map> lines = getAllLine().jsonPath().get();
        assertAll(
                () -> assertThat(lines.stream().map(map -> map.get("name"))).containsAnyOf("2호선"),
                () -> assertThat(lines.stream().map(map -> map.get("color"))).containsAnyOf("green"));
    }

  /**
     * Given 2개의 지하철노선을 생성하고
     * When 지하철노선 목록을 조회하면
     * Then 2개의 지하철노선을 응답 받는다
     */
   @DisplayName("지하철노선을 전부조회한다.")
    @Test
    void getLines() {
        // given
       long gangnamStationId = createStation("강남역").jsonPath().getLong("id");
       long nonhyunStationId = createStation("논현역").jsonPath().getLong("id");
       long kildongStationId = createStation("길동역").jsonPath().getLong("id");
       long sinlimStationId = createStation("신림역").jsonPath().getLong("id");
       createLine(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(gangnamStationId)
                .downStationId(nonhyunStationId)
                .build());
        createLine(LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(kildongStationId)
                .downStationId(sinlimStationId)
                .build());

        // then
        List<Map> lines = getAllLine().jsonPath().get();

        // then
        assertAll(
                () -> assertThat(lines.stream().map(map -> map.get("name"))).containsAnyOf("2호선","1호선"),
                () -> assertThat(lines.stream().map(map -> map.get("color"))).containsAnyOf("green","blue"));
    }


    /**
     * Given 지하철노선을 생성하고
     * When 지하철노선을 조회하면
     * Then 지하철노선의 정보를 응답받는다
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long upStationId = createStation("강남역").jsonPath().getLong("id");
        long downStationId = createStation("논현역").jsonPath().getLong("id");
        long lineId = createLine(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build()).jsonPath().getLong("id");

        // then
        ExtractableResponse<Response> response = getLine(lineId);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("green"),
                () -> assertThat(response.jsonPath().getString("upStationName")).isEqualTo("강남역"),
                () -> assertThat(response.jsonPath().getString("downStationName")).isEqualTo("논현역"));
    }

    /**
     * Given 지하철노선을 생성하고
     * When 존재하지않는 아이디로 지하철노선을 조회하면
     * Then 지하철노선의 정보를 조회할수 없다
     */
    @DisplayName("존재하지않는 아이디로 지하철노선을 조회한다.")
    @Test
    void getLineWithNoExistsId() {
        // when
        ExtractableResponse<Response> response = getLine(-1l);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철노선을 생성하고
     * When 그 지하철노선을 삭제하면
     * Then 그 지하철노선 목록 조회 시 생성한 노선을 찾을 수 없다
     */
/*
    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
        //given
        // given
        createLine(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationName("강남역")
                .downStationName("논현역")
                .build());
        long lineId = createLine(LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationName("철산역")
                .downStationName("김포역")
                .build()).jsonPath().getLong("id");

        //when
        removeLine(lineId);
        ExtractableResponse<Response> response = getAllLine();

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getList("name")).hasSize(1),
                () -> assertThat(response.jsonPath().getList("name")).containsExactlyInAnyOrder("2호선")
        );
    }
*/

    /**
     * Given 지하철노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
/*
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void modifyLine() {
        // given
        long lineId = createLine(LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationName("철산역")
                .downStationName("김포역")
                .build()).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = modifyLine(LineRequest.builder()
                .id(lineId)
                .name("1호선")
                .color("black")
                .upStationName("마산역")
                .downStationName("김포역")
                .build());

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("1호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("black"),
                () -> assertThat(response.jsonPath().getString("upStationName")).isEqualTo("마산역"),
                () -> assertThat(response.jsonPath().getString("downStationName")).isEqualTo("김포역"));
    }
*/

    private ExtractableResponse<Response> createLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getAllLine() {
        return  RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> getLine(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> modifyLine(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines")
                .then().log().all()
                .extract();
    }

    private void removeLine(long id) {
        RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }
    private ExtractableResponse<Response> createStation(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }


}
