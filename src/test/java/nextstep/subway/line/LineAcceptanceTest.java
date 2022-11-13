package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.station.StationAcceptanceTest;
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

import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @Autowired
    DatabaseCleanup databaseCleanup;

    @LocalServerPort
    int port;

    private int upLastStationId;
    private int downLastStationId;
    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }

        // db 초기화
        databaseCleanup.execute();

        createStations();
    }

    private void createStations() {
        upLastStationId = 지하철역을_생성한다("강남역").jsonPath().get("id");
        downLastStationId = 지하철역을_생성한다("역삼역").jsonPath().get("id");
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        //given
        LineRequest request = LineRequest.builder()
                .name("삼성역")
                .color("red darken-2")
                .distance(100)
                .upLastStationId(upLastStationId)
                .downLastStationId(downLastStationId)
                .build();

        //when
        노선을_생성한다(request);
        List<String> allLineNames = 모든_노선_이름을_조회한다();

        //then
        노선_이름이_조회된다(allLineNames, "삼성역");


    }



    public ExtractableResponse<Response> 노선을_생성한다(LineRequest request) {
        return RestAssured.given()
                .body(request).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private List<String> 모든_노선_이름을_조회한다() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private void 노선_이름이_조회된다(List<String> allLineNames, String lineName) {
        assertThat(allLineNames).containsAnyOf(lineName);
    }
}
