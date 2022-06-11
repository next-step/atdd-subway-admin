package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.createStationWithStationName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.BaseAcceptanceTest;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends BaseAcceptanceTest {

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선이 생성된다
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        createStationWithStationName("강남역");
        createStationWithStationName("양재역");

        // when
        ExtractableResponse<Response> response = createLineWithLineName("신분당선", "bg-red-600", 1L, 2L, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        List<String> lineNames = getLineList().jsonPath().getList("name", String.class);
        assertThat(lineNames).containsExactly("신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 기존에 존재하는 지하철 노선을 생성하면
     * Then 지하철 노선 생성이 안된다
     */
    @DisplayName("기존에 존재하는 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicate() {
        // given
        createStationWithStationName("강남역");
        createStationWithStationName("양재역");

        // when
        createLineWithLineName("신분당선", "bg-red-600", 1L, 2L, 10);
        ExtractableResponse<Response> response = createLineWithLineName("신분당선", "bg-red-600", 1L, 2L, 10);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 2개의 지하철 노선을 응답 받는다
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLines() {
        // given
        createStationWithStationName("강남역");
        createStationWithStationName("양재역");
        createStationWithStationName("왕십리역");
        createStationWithStationName("선릉역");
        createLineWithLineName("신분당선", "bg-red-600", 1L, 2L, 10);
        createLineWithLineName("분당선", "bg-green-600", 3L, 4L, 20);

        // when
        ExtractableResponse<Response> response = getLineList();
        List<String> lines = response.body().jsonPath().getList("name", String.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(lines).containsExactly("신분당선","분당선")
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 그 지하철 노선을 삭제하면
     * Then 그 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 없다
     */
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        createStationWithStationName("강남역");
        createStationWithStationName("양재역");
        ExtractableResponse<Response> createResponse = createLineWithLineName("신분당선", "bg-red-600", 1L, 2L, 10);
        Long lineId = createResponse.jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> deleteResponse = deleteLineWithLineId(lineId);

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    /*
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse upStation = createStationWithStationName("강남역").as(StationResponse.class);
        StationResponse downStation = createStationWithStationName("양재역").as(StationResponse.class);
        ExtractableResponse<Response> createLine = createLineWithLineName("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10);

        // when
        LineRequest lineRequest = new LineRequest("신분당선_NEW", "bg-gray-600", downStation.getId(), upStation.getId(), 10);
        ExtractableResponse<Response> updateResponse = updateLineWithLindId(createLine.jsonPath().getLong("id"), lineRequest);

        // then
        ExtractableResponse<Response> updatedLine = findLineById(createLine.jsonPath().getLong("id"));
        assertAll(
            () -> assertThat(updateResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
            () -> assertThat(updatedLine.jsonPath().getString("name")).isEqualTo("신분당선_NEW"),
            () -> assertThat(updatedLine.jsonPath().getString("color")).isEqualTo("bg-gray-600")
        );

    }

    private ExtractableResponse<Response> createLineWithLineName(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = createLineMap(name, color, upStationId, downStationId, distance);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> getLineList() {
        return RestAssured
            .given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> deleteLineWithLineId(Long lineId) {
        return RestAssured
            .given().log().all()
            .when().delete("/lines/{id}", lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> updateLineWithLindId(Long id, LineRequest lineRequest) {
        Map<String, String> params = new HashMap<>();
        params.put("name", lineRequest.getName());
        params.put("color", lineRequest.getColor());

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put("/lines/{id}", id)
            .then().log().all()
            .extract();
    }

    private Map<String, String> createLineMap(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        if (upStationId != null) {
            params.put("upStationId", String.valueOf(upStationId));
        }
        if (downStationId != null) {
            params.put("downStationId", String.valueOf(downStationId));
        }
        params.put("distance", String.valueOf(distance));

        return params;
    }

    public ExtractableResponse<Response> findLineById(Long id) {
        return RestAssured.given().log().all()
            .when().get("/lines/{id}", id)
            .then().log().all()
            .extract();
    }
}
