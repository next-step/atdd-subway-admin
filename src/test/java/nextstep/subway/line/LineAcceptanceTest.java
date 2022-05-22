package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AssertUtils;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.SectionRepository;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.station.StationRestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    @BeforeEach
    void setup(@Autowired LineRepository lineRepository,
               @Autowired SectionRepository sectionRepository,
               @Autowired StationRepository stationRepository) {
        lineRepository.deleteAll();
        sectionRepository.deleteAll();
        stationRepository.deleteAll();
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철노선 생성")
    @Test
    void createLine() {
        long upStationId = toStationId(StationRestAssured.createStation("강남역"));
        long downStationId = toStationId(StationRestAssured.createStation("판교역"));

        ExtractableResponse<Response> response = callCreateLine("신분당선", "bg-red-600", upStationId,
                                                                downStationId, 10L);

        assertAll(
                () -> AssertUtils.assertStatusCode(response, HttpStatus.CREATED),
                () -> {
                    String locationHeaderValue = String.format("/lines/%d", toLineId(response));
                    assertThat(response.header("Location")).isEqualTo(locationHeaderValue);
                },
                () -> assertThat(toLineNames(callGetLines())).containsAnyOf("신분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        long upStationId = toStationId(StationRestAssured.createStation("강남역"));
        long downStationId = toStationId(StationRestAssured.createStation("판교역"));
        callCreateLine("신분당선", "bg-red-600", upStationId,
                       downStationId, 10L);

        ExtractableResponse<Response> response = callCreateLine("신분당선", "bg-red-600", upStationId,
                                                                downStationId, 10L);

        AssertUtils.assertStatusCode(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철노선 목록 조회")
    @Test
    void getLines() {
        long 강남역_id = toStationId(StationRestAssured.createStation("강남역"));
        long 청량리역_id = toStationId(StationRestAssured.createStation("청량리역"));
        long 정자역_id = toStationId(StationRestAssured.createStation("정자역"));
        callCreateLine("신분당선", "bg-red-600", 강남역_id, 정자역_id, 10L);
        callCreateLine("분당선", "bg-yellow-600", 청량리역_id, 정자역_id, 10L);

        ExtractableResponse<Response> response = callGetLines();

        assertAll(
                () -> AssertUtils.assertStatusCode(response, HttpStatus.OK),
                () -> assertThat(toLineNames(response)).containsAnyOf("신분당선", "분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선 조회")
    @Test
    void getLine() {
        long upStationId = toStationId(StationRestAssured.createStation("강남역"));
        long downStationId = toStationId(StationRestAssured.createStation("판교역"));
        long lineId = toLineId(callCreateLine("신분당선", "bg-red-600", upStationId,
                                              downStationId, 10L));

        ExtractableResponse<Response> response = callGetLine(lineId);

        assertAll(
                () -> AssertUtils.assertStatusCode(response, HttpStatus.OK),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선")
        );
    }

    /**
     * When 존재하지 않는 지하철 노선을 조회하면
     * Then 지하철 노선 조회가 안된다.
     */
    @DisplayName("존재하지 않는 지하철노선을 조회한다.")
    @Test
    void getLineWithNotFound() {
        long upStationId = toStationId(StationRestAssured.createStation("강남역"));
        long downStationId = toStationId(StationRestAssured.createStation("판교역"));
        long lineId = toLineId(callCreateLine("신분당선", "bg-red-600", upStationId,
                downStationId, 10L));

        ExtractableResponse<Response> response = callGetLine(lineId);

        assertAll(
                () -> AssertUtils.assertStatusCode(response, HttpStatus.OK),
                () -> assertThat(response.body().jsonPath().getString("name")).isEqualTo("신분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선 수정")
    @Test
    void updateLine() {
        long upStationId = toStationId(StationRestAssured.createStation("강남역"));
        long downStationId = toStationId(StationRestAssured.createStation("판교역"));
        long lineId = toLineId(callCreateLine("신분당선", "bg-red-600", upStationId,
                                              downStationId, 10L));

        ExtractableResponse<Response> response = callUpdateLine(lineId, "아무개선", "bg-blue-600");

        AssertUtils.assertStatusCode(response, HttpStatus.OK);
        ExtractableResponse<Response> getLineResponse = callGetLine(lineId);
        JsonPath jsonPath = getLineResponse.body().jsonPath();
        assertAll(
                () -> assertThat(jsonPath.getString("name")).isEqualTo("아무개선"),
                () -> assertThat(jsonPath.getString("color")).isEqualTo("bg-blue-600")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선 삭제")
    @Test
    void deleteLine() {
        long upStationId = toStationId(StationRestAssured.createStation("강남역"));
        long downStationId = toStationId(StationRestAssured.createStation("판교역"));
        long lineId = toLineId(callCreateLine("신분당선", "bg-red-600", upStationId,
                                              downStationId, 10L));

        callDeleteLine(lineId);

        ExtractableResponse<Response> response = callGetLines();
        assertAll(
                () -> AssertUtils.assertStatusCode(response, HttpStatus.OK),
                () -> assertThat(toLineNames(response)).doesNotContain("신분당선")
        );
    }

    private ExtractableResponse<Response> callCreateLine(String name, String color, long upStationId,
                                                         long downStationId, long distance) {
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

    private ExtractableResponse<Response> callGetLines() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> callGetLine(long lineId) {
        return RestAssured.given().log().all()
                .when().get("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> callUpdateLine(long lineId, String updateName, String updateColor) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", updateName);
        params.put("color", updateColor);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> callDeleteLine(long lineId) {
        return RestAssured.given().log().all()
                .when().delete("/lines/{id}", lineId)
                .then().log().all()
                .extract();
    }

    private long toStationId(ExtractableResponse<Response> response) {
        return AssertUtils.toId(response);
    }

    private long toLineId(ExtractableResponse<Response> response) {
        return AssertUtils.toId(response);
    }

    private List<String> toLineNames(ExtractableResponse<Response> response) {
        return response.body()
                       .jsonPath()
                       .getList("name", String.class);
    }
}
