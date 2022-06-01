package nextstep.subway.line;

import static nextstep.subway.helper.DomainCreationHelper.지하철노선_생성됨;
import static nextstep.subway.helper.DomainCreationHelper.지하철역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.abstracts.DoBeforeEachAbstract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends DoBeforeEachAbstract {

    private Long upStationId;
    private Long downStationId;

    @BeforeEach
    public void setUp() {
        super.setUp();
        upStationId = 지하철역_생성됨("강남역").jsonPath().getLong("id");
        downStationId = 지하철역_생성됨("잠실역").jsonPath().getLong("id");
    }

    /**
     * When 지하철 노선을 생성하면 Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        final ExtractableResponse<Response> response = 지하철노선_생성됨("1호선", "bg-blue-600", upStationId,
                downStationId);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        // then
        final List<String> lineNames = 노선_목록_조회();
        assertThat(lineNames).containsAnyOf("1호선");
    }

    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        지하철노선_생성됨("1호선", "bg-blue-600", upStationId, downStationId);
        지하철노선_생성됨("2호선", "bg-green-600", upStationId, downStationId);

        //when
        final List<String> lineNames = 노선_목록_조회();

        //then
        assertThat(lineNames.size()).isEqualTo(2);
        assertThat(lineNames).containsAnyOf("1호선");
        assertThat(lineNames).containsAnyOf("2호선");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        지하철노선_생성됨("1호선", "bg-blue-600", upStationId, downStationId);
        final Long lineId = 지하철노선_생성됨("2호선", "bg-green-600", upStationId, downStationId).jsonPath()
                .getLong("id");

        //when
        final ExtractableResponse<Response> response = 노선_조회(lineId);

        //then
        assertThat(response.jsonPath().getLong("id")).isEqualTo(lineId);
        assertThat(response.jsonPath().getString("name")).isEqualTo("2호선");
        assertThat(response.jsonPath().getString("color")).isEqualTo("bg-green-600");
    }

    /**
     * 지하철 노선을 생성하고 When 생성한 지하철 노선을 수정하면 Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        final Long lineId = 지하철노선_생성됨("1호선", "bg-blue-600", upStationId, downStationId).jsonPath()
                .getLong("id");

        //when
        final Map<String, Object> params = new HashMap<>();
        params.put("name", "다른1호선");
        params.put("color", "bg-blue-700");

        final ExtractableResponse<Response> updateResponse = 노선_수정됨(lineId, params);

        //then
        assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        final ExtractableResponse<Response> selectResponse = 노선_조회(lineId);

        assertThat(selectResponse.jsonPath().getString("name")).isEqualTo("다른1호선");
        assertThat(selectResponse.jsonPath().getString("color")).isEqualTo("bg-blue-700");
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 삭제하면 Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철 노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        지하철노선_생성됨("1호선", "bg-blue-600", upStationId, downStationId);
        final Long lineId = 지하철노선_생성됨("2호선", "bg-green-600", upStationId, downStationId).jsonPath()
                .getLong("id");

        //when
        노선_삭제됨(lineId);

        //then
        final List<String> lineNames = 노선_목록_조회();

        //then
        assertThat(lineNames.size()).isEqualTo(1);
        assertThat(lineNames).containsAnyOf("1호선");
        assertThat(lineNames).doesNotContain("2호선");
    }

    private ValidatableResponse 노선_삭제됨(Long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all();
    }

    private List<String> 노선_목록_조회() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract().jsonPath().getList("name", String.class);
    }

    private ExtractableResponse<Response> 노선_조회(Long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/" + lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 노선_수정됨(Long lineId, Map<String, Object> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all()
                .extract();
    }
}
