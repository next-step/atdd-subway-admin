package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.Isolationer;
import nextstep.subway.dto.LineDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

public class LineAcceptanceTest {

    @LocalServerPort
    int port;
    @Autowired
    private Isolationer dbIsolation;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        dbIsolation.excute();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        LineDto.Request lineRequest = new LineDto.Request()
                .setName("신분당선")
                .setColor("bg-red-600")
                .setUpStationId(1)
                .setDownStationId(2)
                .setDistance(10);


        // then

    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getAllLine() {
        // given


        // when


        // then

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given


        // when


        // then
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void modifyLine() {
        // given


        // when


        // then

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given


        // when


        // then

    }

    public static ExtractableResponse<Response> 지하철_노선을_생성한다(LineDto.Request line) {

        return RestAssured.given()
                .body(line).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then()
                .log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 모든_노선을_조회한다() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 모든_노선을_조회한다(int lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/"+lineId)
                .then().log().all()
                .extract();
    }


    public static void 노선_2개를_생성한다() {

        지하철_노선을_생성한다(new LineDto.Request()
                .setName("신분당선")
                .setColor("bg-red-600")
                .setUpStationId(1)
                .setDownStationId(2)
                .setDistance(10));

        지하철_노선을_생성한다(new LineDto.Request()
                .setName("분당선")
                .setColor("bg-green-600")
                .setUpStationId(1)
                .setDownStationId(3)
                .setDistance(10));
    }

    public static ExtractableResponse<Response> 지하철_노선을_수정한다(LineDto.Request line, int lineId) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/"+lineId)
                .then().log().all()
                .extract();

    }

    public ExtractableResponse<Response> 해당_노선을_제거한다(int stationId) {
        return RestAssured.given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/" + stationId)
                .then().log().all()
                .extract();
    }



}
