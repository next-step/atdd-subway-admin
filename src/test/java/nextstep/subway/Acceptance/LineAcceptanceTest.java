package nextstep.subway.Acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AbstractAcceptanceTest {

    private Long stationId1;
    private Long stationId2;
    private Long stationId3;

    @BeforeEach
    public void setUpData() {
        stationId1 = 지하철역_신규_생성_및_아이디_요청("강남역");
        stationId2 = 지하철역_신규_생성_및_아이디_요청("양재역");
        stationId3 = 지하철역_신규_생성_및_아이디_요청("교대역");
    }

    /**
     * When 지하철 노선을 생성하면
     * then 지하철 노선에 등록되고,
     * then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선 생성을 생성한다.")
    @Test
    void create() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_신규_생성_요청("신분당선", "red", stationId1, stationId2);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        //then
        List<String> names = 지하철_노선_목록_조회().getList("name", String.class);
        assertThat(names).containsExactly("신분당선");
    }

    /**
     * When 지하철 노선을 생성하면
     * then 기존에 존재하는 지하철 노선 이름으로 지하철역을 생성하면
     * then 지하철 노선 생성이 안된다.
     */
    @DisplayName("동일한 이름으로 노선 생성 요청하면 노선이 생성되지 않는다.")
    @Test
    void create_same_name() {
        //when
        지하철_노선_신규_생성_요청("신분당선", "red", stationId1, stationId2);

        //then
        ExtractableResponse<Response> response = 지하철_노선_신규_생성_요청("신분당선", "blue", stationId1, stationId2);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * When 지하철 노선을 생성하면
     * then 기존에 존재하는 지하철 노선 색으로 지하철역을 생성하면
     * then 지하철 노선 생성이 안된다.
     */
    @DisplayName("동일한 노선 색으로 노선 생성 요청하면 노선이 생성되지 않는다.")
    @Test
    void create_same_color() {
        //when
        지하철_노선_신규_생성_요청("신분당선", "red", stationId1, stationId2);

        //then
        ExtractableResponse<Response> response = 지하철_노선_신규_생성_요청("2호선", "red", stationId1, stationId2);

        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * given 지하철 노산을 2개 등록하고
     * when 지하철 노선 목록을 조회하면
     * then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록 조회하면 등록한 노선을 조회할 수 있다.")
    @Test
    void get_lines() {
        String 신분당선 = "신분당선";
        String 이호선 = "2호선";
        //given
        지하철_노선_신규_생성_요청(신분당선, "red", stationId1, stationId2);
        지하철_노선_신규_생성_요청(이호선, "blue", stationId1, stationId3);

        //when
        List<String> names = 지하철_노선_목록_조회().getList("name", String.class);
        //then
        assertThat(names).containsExactlyInAnyOrder(신분당선, 이호선);

    }

    /**
     * given 지하철 노선 한개 등록하고
     * when 생성한 지하철 노선을 조회하면
     * then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     * then 노선에 등록되 역 정보를 응답 받을 수 잇다.
     */
    @DisplayName("지하철 노선 조회하면 등록한 노선을 조회할 수 있다.")
    @Test
    void get_lines_with_id() {
        //given
        int id = 지하철_노선_신규_생성_요청("신분당선", "red", stationId1, stationId2).jsonPath().get("id");

        //when
        JsonPath response = 지하철_노선_단일_조회(id);
        int responseId = response.getInt("id");
        List<Integer> stationsIds = response.getList("stations.id");

        //then
        assertThat(id).isEqualTo(responseId);
        assertThat(stationsIds).containsExactlyInAnyOrder((int)(long)stationId1, (int)(long)stationId2);
    }

    /**
     * given 지하철 노선 한개 등록하고
     * when 생성한 지하철 노선 하나를 수정하고
     * then 생성한 지하철 노선을 조회하면 수정된 지하철 노선 하나가 조회된다.
     */
    @DisplayName("등록된 지하철 노선의 이름과 색깔을 변경할 수 있다.")
    @Test
    void update_line() {
        //given
        String newName = "다른분당선";
        String newColor = "blue";
        int id = 지하철_노선_신규_생성_요청("신분당선", "red", stationId1, stationId2).jsonPath().get("id");

        //when
        지하철_노선_정보_수정_요청(id, newName, newColor);
        //then
        JsonPath response = 지하철_노선_단일_조회(id);
        assertThat(response.getString("name")).isEqualTo(newName);
        assertThat(response.getString("color")).isEqualTo(newColor);
    }

    /**
     * given 지하철 노선 한개 등록하고
     * when 지하철 노선을 삭제하면
     * then 지하철 노선 목록 조회시 생성한 노선을 확인할 수 없다.
     */
    @DisplayName("등록된 지하철 노선을 삭제할 수 있다.")
    @Test
    void deleteLine() {
        //given
        int id = 지하철_노선_신규_생성_요청("신분당선", "red", stationId1, stationId2).jsonPath().get("id");

        //when
        지하철_노선_삭제_요청(id);

        //then
        assertThat(지하철_노선_목록_조회().getList("id")).doesNotContain(id);

    }

    private Long 지하철역_신규_생성_및_아이디_요청(String name) {
        return Long.parseLong(지하철역_신규_생성_요청(name).jsonPath().getString("id"));
    }


    private JsonPath 지하철_노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    private JsonPath 지하철_노선_단일_조회(int id) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract().jsonPath();
    }

    private JsonPath 지하철_노선_정보_수정_요청(int lineId, String newName, String newColor) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("name", newName);
        paramMap.put("color", newColor);

        return RestAssured.given().log().all()
                .body(paramMap)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract().jsonPath();
    }

    private void 지하철_노선_삭제_요청(int id) {
        RestAssured.given().log().all()
                .when().delete("/lines/" + id)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

}
