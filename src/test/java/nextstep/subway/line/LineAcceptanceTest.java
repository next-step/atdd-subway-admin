package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.utils.DatabaseCleanUtil;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;

    @Autowired
    DatabaseCleanUtil databaseCleanUtil;

    StationResponse 잠실역;
    StationResponse 몽촌토성역;
    StationResponse 교대역;

    @BeforeEach
    public void setUp(){
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleanUtil.cleanUp();

        잠실역 = 지하철역_1개_생성("잠실역").as(StationResponse.class);
        몽촌토성역 = 지하철역_1개_생성("몽촌토성역").as(StationResponse.class);
        교대역 = 지하철역_1개_생성("교대역").as(StationResponse.class);
    }

    /**
     * given
     * when 지하철 노선을 생성하면
     * then 지하철 노선 목록 조회 시, 생성한 노선이 조회된다.
     */
    @Test
    @DisplayName("지하철 노선 생성")
    void createLine() {
        //given

        //when
        ExtractableResponse<Response> extract =
                지하철_노선_1개_생성("2호선", "bg-color-060", 교대역.getId(), 잠실역.getId(), 10);

        // then
        assertThat(extract.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        assertThat(지하철_노선_목록_조회().jsonPath().getList("name", String.class))
                .containsAnyOf("2호선");
    }

    /**
     * given 2개의 지하철 노선을 생성하고
     * when 지하철 노선 목록을 조회하면
     * then 지하철 노선 목록 조회 시, 2개의 노선을 조회 할 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 목록 조회")
    void findAllLines(){
        // given
        지하철_노선_1개_생성("2호선", "bg-color-060", 교대역.getId(), 잠실역.getId(), 10);
        지하철_노선_1개_생성("8호선", "bg-color-440", 잠실역.getId(), 몽촌토성역.getId(), 5);

        // when
        ExtractableResponse<Response> extract = 지하철_노선_목록_조회();

        // then
        assertThat(extract.jsonPath().getList("name", String.class))
                .containsExactly("2호선", "8호선");
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선 조회하면
     * then 생성한 지하철 노선의 정보를 응답 받을 수 있다.
     */
    @Test
    @DisplayName("지하철 노선 조회")
    void findLine(){
        // given
        ExtractableResponse<Response> created = 지하철_노선_1개_생성("2호선", "bg-color-060", 교대역.getId(), 잠실역.getId(), 10);

        // when
        ExtractableResponse<Response> result = 지하철_노선_정보_조회(created.jsonPath().get("id"));

        // then
        assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(created.jsonPath().get("name").toString())
                .isEqualTo(result.jsonPath().get("name").toString());
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 수정하면
     * then 해당 지하철 노선 정보는 수정된다.
     */
    @Test
    @DisplayName("지하철 노선 수정")
    void updateLine() {
        // given
        ExtractableResponse<Response> created = 지하철_노선_1개_생성("2호선", "bg-color-060", 교대역.getId(), 잠실역.getId(), 10);

        // when
        ExtractableResponse<Response> updated =
                지하철_노선_1개_수정(created.jsonPath().get("id"), "3호선", "bg-color-006");

        // then
        assertThat(updated.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    /**
     * given 지하철 노선을 생성하고
     * when 생성한 지하철 노선을 삭제하면
     * then 해당 지하철 노선 정보는 삭제된다.(지하철 노선 전체 목록 조회 시, 조회되지 않는다.)
     */
    @Test
    @DisplayName("지하철 노선 삭제")
    void delete(){
        // given
        ExtractableResponse<Response> created = 지하철_노선_1개_생성("2호선", "bg-color-060", 교대역.getId(), 잠실역.getId(), 10);

        // when
        ExtractableResponse<Response> deleted = 지하철_노선_1개_삭제(created.jsonPath().get("id"));

        // then
        assertThat(deleted.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(지하철_노선_목록_조회().jsonPath().getList("name", String.class))
                .doesNotContain("2호선");
    }

    private ExtractableResponse<Response> 지하철_노선_1개_삭제(Object id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_1개_생성(String name, String color, Long upStationId, Long downStationId, Integer distance) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("color", color);
        param.put("upStationId", upStationId);
        param.put("downStationId", downStationId);
        param.put("distance", distance);

        return RestAssured.given().log().all()
                .body(param)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_정보_조회(Object id) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_1개_수정(Object id, String name, String color) {
        Map<String, Object> param = new HashMap<>();
        param.put("name", name);
        param.put("color", color);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(param)
                .when().put("/lines/{id}", id)
                .then().log().all()
                .extract();
    }
}
