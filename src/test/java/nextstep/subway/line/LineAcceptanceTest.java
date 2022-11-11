package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static nextstep.subway.station.StationAcceptanceTest.지하철_역_등록;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 기능")
@Sql(scripts = "classpath:truncateTables.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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
        //when
        long gangnamStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long nonhyunStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        지하철_노선_등록(LineRequest.builder()
                .name("2호선")
                .color("green")
                .distance(10l)
                .upStationId(gangnamStationId)
                .downStationId(nonhyunStationId)
                .build());
        //then
        List<LineResponse> lines = 지하철_노선_전체조회().jsonPath().getList(".", LineResponse.class).stream().collect(Collectors.toList());
        assertAll(
                () -> assertThat(lines.stream().map(LineResponse::getName).collect(Collectors.toList())).containsAnyOf("2호선"),
                () -> assertThat(lines.stream().map(LineResponse::getColor).collect(Collectors.toList())).containsAnyOf("green"),
                () -> assertThat(lines.stream().flatMap(lineResponse -> lineResponse.getStations().stream().map(StationResponse::getName))
                        .collect(Collectors.toList())).containsAnyOf("강남역", "논현역"));
    }

    /**
     * Given 2개의 지하철노선을 생성하고
     * When 지하철노선 목록을 조회하면
     * Then 2개의 지하철노선을 응답 받는다
     */
    @DisplayName("지하철노선을 전부조회한다.")
    @Test
    void getLines() {
        //given
        long gangnamStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long nonhyunStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        long kildongStationId = 지하철_역_등록("길동역").jsonPath().getLong("id");
        long sinlimStationId = 지하철_역_등록("신림역").jsonPath().getLong("id");
        지하철_노선_등록(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(gangnamStationId)
                .downStationId(nonhyunStationId)
                .build());
        지하철_노선_등록(LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(kildongStationId)
                .downStationId(sinlimStationId)
                .build());

        //when
        List<LineResponse> lines = 지하철_노선_전체조회().jsonPath().getList(".", LineResponse.class)
                .stream().collect(Collectors.toList());

        //then
        assertAll(
                () -> assertThat(lines.stream().map(LineResponse::getName).collect(Collectors.toList())).containsAnyOf("2호선", "1호선"),
                () -> assertThat(lines.stream().map(LineResponse::getColor).collect(Collectors.toList())).containsAnyOf("green", "blue"),
                () -> assertThat(lines.stream().flatMap(lineResponse -> lineResponse.getStations().stream().map(StationResponse::getName))
                        .collect(Collectors.toList())).containsAnyOf("강남역", "논현역", "길동역", "심림역"));
    }


    /**
     * Given 지하철노선을 생성하고
     * When 지하철노선을 조회하면
     * Then 지하철노선의 정보를 응답받는다
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        //given
        long upStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long downStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build()).jsonPath().getLong("id");

        //then
        ExtractableResponse<Response> response = 지하철_노선_조회(lineId);

        //then
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.jsonPath().getString("name")).isEqualTo("2호선"),
                () -> assertThat(response.jsonPath().getString("color")).isEqualTo("green"),
                () -> assertThat(response.jsonPath()
                        .getList("stations", StationResponse.class).stream().map(StationResponse::getName)
                        .collect(Collectors.toList())).containsAnyOf("강남역", "논현역"));
    }

    /**
     * Given 지하철노선을 생성하고
     * When 존재하지않는 아이디로 지하철노선을 조회하면
     * Then 지하철노선의 정보를 조회할수 없다
     */
    @DisplayName("존재하지않는 아이디로 지하철노선을 조회한다.")
    @Test
    void getLineWithNoExistsId() {
        //given
        long upStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long downStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        지하철_노선_등록(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build()).jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회(-1L);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철노선을 생성하고
     * When 생성한 지하철노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
        //given
        long gangnamStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long nonhyunStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        long kildongStationId = 지하철_역_등록("길동역").jsonPath().getLong("id");
        long sinlimStationId = 지하철_역_등록("신림역").jsonPath().getLong("id");
        지하철_노선_등록(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(gangnamStationId)
                .downStationId(nonhyunStationId)
                .build());
        long lineId = 지하철_노선_등록(LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(kildongStationId)
                .downStationId(sinlimStationId)
                .build()).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_노선_삭제(lineId);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /**
     * Given 지하철노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void modifyLine() {
        //given
        long upStationId = 지하철_역_등록("강남역").jsonPath().getLong("id");
        long downStationId = 지하철_역_등록("논현역").jsonPath().getLong("id");
        long lineId = 지하철_노선_등록(LineRequest.builder()
                .name("2호선")
                .color("green")
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build()).jsonPath().getLong("id");

        //when
        ExtractableResponse<Response> response = 지하철_노선_수정(lineId, LineRequest.builder()
                .name("1호선")
                .color("black")
                .upStationId(upStationId)
                .downStationId(downStationId)
                .build());

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

    }

    private ExtractableResponse<Response> 지하철_노선_등록(LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_전체조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정(long id, LineRequest lineRequest) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/lines/{id}")
                .then().log().all()
                .extract();
    }
}

