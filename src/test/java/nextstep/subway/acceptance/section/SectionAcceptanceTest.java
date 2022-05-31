package nextstep.subway.acceptance.section;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.acceptance.base.BaseAcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철구간 관련 기능")
class SectionAcceptanceTest extends BaseAcceptanceTest {
    StationResponse 강남역;
    StationResponse 양재역;
    StationResponse 청계산입구역;
    StationResponse 판교역;
    StationResponse 정자역;
    LineResponse 신분당선;
    @BeforeEach
    protected void setUp() {
        super.setUp();
        강남역 = 지하철역_생성_요청("강남역").as(StationResponse.class);
        양재역 = 지하철역_생성_요청("양재역").as(StationResponse.class);
        청계산입구역 = 지하철역_생성_요청("청계산입구역").as(StationResponse.class);
        판교역 = 지하철역_생성_요청("판교역").as(StationResponse.class);
        정자역 = 지하철역_생성_요청("정자역").as(StationResponse.class);
        신분당선 = 지하철노선_생성_요청("신분당선", "bg-red-600", 양재역.getId(), 판교역.getId(), 10).as(LineResponse.class);
    }

    /**
     * When 지하철 노선의 구간 사이에 새로운 구간을 등록하면 (상행역 일치)
     * Then 지하철 노선에 사이에 새로운 역이 등록된다.
     */
    @DisplayName("지하철 노선의 구간 사이에 지하철구간 생성 (상행역 일치)")
    @Test
    void addSectionInsideIfEqualUpStation() {
        // when
        ExtractableResponse<Response> response = 지하철구간_추가_요청(신분당선.getId(), 양재역.getId(), 청계산입구역.getId(), 5);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).containsOnly(양재역.getName(), 청계산입구역.getName(), 판교역.getName());
    }

    /**
     * When 지하철 노선의 구간 사이에 새로운 구간을 등록하면 (하행역 일치)
     * Then 새로운 구간이 등록된다.
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("지하철 노선의 구간 사이에 지하철구간 생성 (하행역 일치)")
    @Test
    void addSectionInsideIfEqualDownStation() {
        // when
        ExtractableResponse<Response> response = 지하철구간_추가_요청(신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 3);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).hasSize(3);
        assertThat(stationNames).containsOnly(양재역.getName(), 청계산입구역.getName(), 판교역.getName());
    }

    /**
     * Given 지하철 노선의 구간 사이에 새로운 구간을 등록하고
     * When 새로운 구간을 상행 종점에 등록하면
     * Then 새로운 구간이 등록된다.
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("새로운 구간을 상행 종점에 등록한다.")
    @Test
    void addSectionAtUpStation() {
        // given
        지하철구간_추가_요청(신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철구간_추가_요청(신분당선.getId(), 강남역.getId(), 양재역.getId(), 7);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).hasSize(4);
        assertThat(stationNames).containsOnly(강남역.getName(), 양재역.getName(), 청계산입구역.getName(), 판교역.getName());
    }

    /**
     * Given 지하철 노선의 구간 사이에 새로운 구간을 등록하고
     * When 새로운 구간을 하행 종점에 등록하면
     * Then 새로운 구간이 등록된다.
     * Then 새로운 역이 등록된다.
     */
    @DisplayName("새로운 구간을 하행 종점에 등록한다.")
    @Test
    void addSectionAtDownStation() {
        // given
        지하철구간_추가_요청(신분당선.getId(), 청계산입구역.getId(), 판교역.getId(), 5);

        // when
        ExtractableResponse<Response> response = 지하철구간_추가_요청(신분당선.getId(), 판교역.getId(), 정자역.getId(), 7);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).hasSize(4);
        assertThat(stationNames).containsOnly(양재역.getName(), 청계산입구역.getName(), 판교역.getName(), 정자역.getName());
    }

    /**
     * When 지하철 노선의 구간 사이에 구간 길이가 크거나 같은 새로운 구간을 등록하면
     * Then 구간 등록에 실패한다.
     */
    @DisplayName("구간 사이에 새로운 구간을 등록할 경우 기존 구간 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @ParameterizedTest(name = "구간 10 사이에 새로운 구간 {0}을 등록할 경우 기존 구간 사이 길이보다 크거나 같아 등록을 할 수 없음")
    @ValueSource(strings = {"10", "12"})
    void addSectionInsideByEqualOrLongerDistance(int input) {
        // when
        ExtractableResponse<Response> response = 지하철구간_추가_요청(신분당선.getId(), 양재역.getId(), 청계산입구역.getId(), input);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    ExtractableResponse<Response> 지하철구간_추가_요청(long lineId, long upStationId, long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post( "/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, long upStationId, long downStationId, int distance) {
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

    ExtractableResponse<Response> 지하철역_생성_요청(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/stations")
                .then().log().all()
                .extract();
    }
}
