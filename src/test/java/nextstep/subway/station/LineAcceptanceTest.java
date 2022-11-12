package nextstep.subway.station;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends AbstractAcceptanceTest {

    Long upStationId;
    Long downStationId;

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철노선이 생성된다
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void createLine() {
        // when
        setUpStationData("강남역", "역삼역");
        String lineName = "2호선";
        String lineColor = "bg-green";
        ExtractableResponse<Response> response = 지하철노선_등록_요청(lineName, lineColor, upStationId, downStationId, 6L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        List<String> lineNames = 지하철노선_목록_조회_요청().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsAnyOf(lineName);
    }

    /**
     * Given 지하철노선을 생성하고
     * When 기존에 존재하는 지하철노선 이름으로 지하철노선을 생성하면
     * Then 지하철노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철노선 이름으로 지하철노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        setUpStationData("강남역", "역삼역");
        String lineName = "2호선";
        String lineColor = "bg-green";
        지하철노선_등록_요청(lineName, lineColor, upStationId, downStationId, 6L);

        // when
        ExtractableResponse<Response> response = 지하철노선_등록_요청(lineName, lineColor, upStationId, downStationId, 6L);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        setUpStationData("노원역", "이수역");
        지하철노선_등록_요청("4호선", "bg-blue", upStationId, downStationId, 16L);
        지하철노선_등록_요청("7호선", "bg-khaki", upStationId, downStationId, 14L);

        // when
        ExtractableResponse<Response> response = 지하철노선_목록_조회_요청();

        // then
        assertThat(response.jsonPath().getList("name", String.class)).hasSize(2);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        setUpStationData("상수역", "합정역");
        ExtractableResponse<Response> createResponse = 지하철노선_등록_요청("6호선", "bg-brown", upStationId, downStationId, 2L);

        // when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(createResponse.jsonPath().getLong("id"));

        // then
        assertAll(
                () -> assertThat(getResponse.jsonPath().getString("name")).isEqualTo("6호선"),
                () -> assertThat(getResponse.jsonPath().getString("color")).isEqualTo("bg-brown")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 이름과 색상을 수정한다.")
    @Test
    void updateLine() {
        // given
        setUpStationData("인천역", "시청역");
        ExtractableResponse<Response> createResponse = 지하철노선_등록_요청("1호선", "bg-darkblue", upStationId, downStationId, 40L);
        Long id = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> updateResponse = 지하철노선_수정_요청(id, "다른1호선","bg-darkblue-100");
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(id);

        // then
        assertAll(
                () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(getResponse.jsonPath().getLong("id")).isEqualTo(id),
                () -> assertThat(getResponse.jsonPath().getString("name")).isEqualTo("다른1호선"),
                () -> assertThat(getResponse.jsonPath().getString("color")).isEqualTo("bg-darkblue-100")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        setUpStationData("도곡역", "대청역");
        ExtractableResponse<Response> createResponse = 지하철노선_등록_요청("3호선", "bg-orange", upStationId, downStationId, 5L);
        Long id = createResponse.jsonPath().getLong("id");

        // when
        지하철노선_삭제_요청(id);

        // then
        List<String> lineNames = 지하철노선_목록_조회_요청().jsonPath().getList("name", String.class);
        assertThat(lineNames).doesNotContain("3호선");
    }

    ExtractableResponse<Response> 지하철노선_등록_요청(String name, String color, Long upStationId, Long downStationId, Long distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    private void setUpStationData(String upStationName, String downStationName) {
        upStationId = 지하철역_등록_요청(upStationName).jsonPath().getLong("id");
        downStationId = 지하철역_등록_요청(downStationName).jsonPath().getLong("id");
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
                .pathParam("id", id)
                .when().get("/lines/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_수정_요청(Long id, String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", id)
                .when().put("/lines/{id}")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }

    private ExtractableResponse<Response> 지하철노선_삭제_요청(Long id) {
        return  RestAssured.given().log().all()
                .pathParam("id", id)
                .when().delete("/lines/{id}")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .extract();
    }

    private ExtractableResponse<Response> 지하철역_등록_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract();
    }
}
