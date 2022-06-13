package nextstep.subway.section;

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
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.line.LineAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends BaseAcceptanceTest {

    private final LineAcceptanceTest lineAcceptanceTest = new LineAcceptanceTest();
    private Long 강남역_id;
    private Long 판교역_id;
    private Long 정자역_id;
    private Long 신분당선_id;
    private Long 청계천역_id;
    private Long 광교역_id;
    private Long 양재역_id;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역_id = createStationWithStationName("강남역").jsonPath().getLong("id");
        //판교역_id = createStationWithStationName("판교역").jsonPath().getLong("id");
        정자역_id = createStationWithStationName("정자역").jsonPath().getLong("id");

        신분당선_id = createLineWithLineName("신분당선", "bg-red-600", 1L, 2L, 10).jsonPath().getLong("id");
    }

    /**
     * Given 지하철 노선에 지하철역 등록 요청
     * When 지하철 노선에 새로운 구간을 등록하면
     * Then 지하철 노선에 구간이 등록된다.
     */
    @Test
    @DisplayName("구간을 등록한다.")
    void addSection() {
        // given
        Long 양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 강남역_id, 양재역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * When 지하철 노선의 새로운 역을 상행 종점으로 구간을 등록하면
     * Then 새로운 구간이 등록된다.
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionAtFront() {
        // given
        Long 신사역_id = createStationWithStationName("신사역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 신사역_id, 강남역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * When 지하철 노선의 새로운 역을 하행 종점으로 구간을 등록하면
     * Then 새로운 구간이 등록된다.
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAtEnd() {
        // given
        Long 광교역_id = createStationWithStationName("광교역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 정자역_id, 광교역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 지하철 노선에 지하철역 등록 요청
     * When 지하철 노선의 구간 사이에 구간 길이가 크거나 같은 새로운 구간을 등록하면
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("구간 사이에 새로운 구간을 등록할 때 기존 구간 길이보다 크거나 같으면 등록불가")
    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void addSectionWithInvalidDistanceException(int input) {
        // given
        Long 양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 강남역_id, 양재역_id, input);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선의 구간 사이에 새로운 구간을 등록하고
     * When 상행역과 하행역이 이미 노선에 모두 등록되어 있는 구간을 등록하면
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("지하철 노선의 구간 등록시 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSectionWithDuplication() {
        // given
        Long 양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");
        createSection(신분당선_id, 강남역_id, 양재역_id, 5);

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 양재역_id, 정자역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 포함되지 않은 역을 생성하고
     * When 상행역과 하행역 둘 중 하나도 포함되어 있지 않으면
     * Then 구간 추가에 실패한다.
     */
    @DisplayName("지하철 노선의 구간 등록시 상행역과 하행역 둘 중 하나도 포함되어 있지 않다면 추가할 수 없음")
    @Test
    void addSectionWithStationNotExist() {
        // given
        Long 논현역_id = createStationWithStationName("논현역").jsonPath().getLong("id");
        Long 신논현역_id = createStationWithStationName("신논현역").jsonPath().getLong("id");

        // when
        ExtractableResponse<Response> response = createSection(신분당선_id, 논현역_id, 신논현역_id, 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 중간역을 제거하면
     * Then 해당 역이 제거된다.
     */
    @DisplayName("지하철 노선의 중간역을 제거한다.")
    @Test
    void deleteMiddleStation() {
        // given
        광교역_id = createStationWithStationName("광교역").jsonPath().getLong("id");
        판교역_id = createStationWithStationName("판교역").jsonPath().getLong("id");
        양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");
        createSection(신분당선_id, 정자역_id, 광교역_id, 10);
        createSection(신분당선_id, 강남역_id, 판교역_id, 5);
        createSection(신분당선_id, 강남역_id, 양재역_id, 1);

        // when
        ExtractableResponse<Response> response = deleteSection(신분당선_id, 판교역_id);
        ExtractableResponse<Response> findLine = lineAcceptanceTest.findLineById(신분당선_id);
        List<String> stations = findLine.jsonPath().getList("stations.name", String.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> assertThat(stations.size()).isEqualTo(4),
            () -> assertThat(stations).contains("양재역","광교역","정자역","강남역")
        );

    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 상행역 종점을 제거하면
     * Then 해당 종점이 제거된다.
     */
    @DisplayName("지하철 노선의 상행역 종점을 제거한다.")
    @Test
    void deleteFirstStation() {
        // given
        광교역_id = createStationWithStationName("광교역").jsonPath().getLong("id");
        판교역_id = createStationWithStationName("판교역").jsonPath().getLong("id");
        양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");
        createSection(신분당선_id, 정자역_id, 광교역_id, 10);
        createSection(신분당선_id, 강남역_id, 판교역_id, 5);
        createSection(신분당선_id, 강남역_id, 양재역_id, 1);

        // when
        ExtractableResponse<Response> response = deleteSection(신분당선_id, 강남역_id);
        ExtractableResponse<Response> findLine = lineAcceptanceTest.findLineById(신분당선_id);
        List<String> stations = findLine.jsonPath().getList("stations.name", String.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> assertThat(stations.size()).isEqualTo(4),
            () -> assertThat(stations).contains("양재역","판교역","정자역","광교역")
        );

    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 하행역 종점을 제거하면
     * Then 해당 종점이 제거된다.
     */
    @DisplayName("지하철 노선의 하행역 종점을 제거한다.")
    @Test
    void deleteLastStation() {
        // given
        광교역_id = createStationWithStationName("광교역").jsonPath().getLong("id");
        판교역_id = createStationWithStationName("판교역").jsonPath().getLong("id");
        양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");
        createSection(신분당선_id, 정자역_id, 광교역_id, 10);
        createSection(신분당선_id, 강남역_id, 판교역_id, 5);
        createSection(신분당선_id, 강남역_id, 양재역_id, 1);

        // when
        ExtractableResponse<Response> response = deleteSection(신분당선_id, 광교역_id);
        ExtractableResponse<Response> findLine = lineAcceptanceTest.findLineById(신분당선_id);
        List<String> stations = findLine.jsonPath().getList("stations.name", String.class);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
            () -> assertThat(stations.size()).isEqualTo(4),
            () -> assertThat(stations).contains("양재역","판교역","정자역","강남역")
        );

    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 노선에 존재하지 않는 역을 제거하면
     * Then 구간 제거할 때 예외가 발생한다.
     */
    @DisplayName("노선에 존재하지 않는 역을 제거하면 구간 제거할 때 예외가 발생한다.")
    @Test
    void deleteSectionNotExists() {
        // given
        광교역_id = createStationWithStationName("광교역").jsonPath().getLong("id");
        판교역_id = createStationWithStationName("판교역").jsonPath().getLong("id");
        양재역_id = createStationWithStationName("양재역").jsonPath().getLong("id");
        createSection(신분당선_id, 정자역_id, 광교역_id, 10);
        createSection(신분당선_id, 강남역_id, 판교역_id, 5);
        createSection(신분당선_id, 강남역_id, 양재역_id, 1);

        // when
        ExtractableResponse<Response> response = deleteSection(신분당선_id, 청계천역_id);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );

    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 구간이 하나인 노선에서 역을 제거하면
     * Then 구간 제거할 때 예외가 발생한다.
     */
    @DisplayName("구간이 하나인 노선에서 역을 제거하면 구간 제거할 때 예외가 발생한다.")
    @Test
    void deleteSectionOnlyOne() {
        // when
        ExtractableResponse<Response> response = deleteSection(신분당선_id, 정자역_id);

        // then
        assertAll(
            () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
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

    public static ExtractableResponse<Response> createSection(Long lineId, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured
            .given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post( "/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> deleteSection(Long lineId, Long stationId) {
        return RestAssured
            .given().log().all()
            .param("stationId", stationId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().delete("/lines/{id}/sections", lineId)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> getLine(LineResponse lineResponse) {
        return RestAssured
            .given().log().all()
            .when()
            .get("/lines/{id}", lineResponse.getId())
            .then().log().all()
            .extract();
    }
}
