package nextstep.subway.station;

import static nextstep.subway.station.CreateFactory.지하철노선_등록_요청;
import static nextstep.subway.station.CreateFactory.지하철역_등록_요청;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

public class SectionAcceptanceTest extends AbstractAcceptanceTest {

    private StationResponse 신사역;
    private StationResponse 강남역;
    private StationResponse 양재시민의숲;
    private StationResponse 판교역;
    private StationResponse 광교역;
    private LineResponse 신분당선;
    private Long lineDistance;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        lineDistance = 10L;
        신사역 = 지하철역_등록_요청("신사역").as(StationResponse.class);
        강남역 = 지하철역_등록_요청("강남역").as(StationResponse.class);
        양재시민의숲 = 지하철역_등록_요청("양재시민의숲").as(StationResponse.class);
        판교역 = 지하철역_등록_요청("판교역").as(StationResponse.class);
        광교역 = 지하철역_등록_요청("광교역").as(StationResponse.class);

        신분당선 = 지하철노선_등록_요청("신분당선", "bg-red-600", 강남역.getId(), 판교역.getId(), lineDistance).as(LineResponse.class);
    }

    /**
     * When 노선 내 역들 사이에 새로운 역을 생성하면
     * Then 노선 중간에 새로운 구간이 생성된다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addStationBetweenLine() {
        // when
        Long currentDistance = 5L;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재시민의숲.getId(), currentDistance);

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        Long distance = response.jsonPath().getLong("distance");
        assertAll(
                () -> assertThat(stationNames).containsExactly(강남역.getName(), 양재시민의숲.getName(), 판교역.getName()),
                () -> assertThat(stationNames).hasSize(3),
                () -> assertThat(distance).isEqualTo(lineDistance)
        );
    }

    /**
     * When 노선의 상행 종점을 다음역으로 하는 새로운 역을 생성하면
     * Then 노선에 새로운 구간이 생성된다
     * Then 노선의 상행 종점역이 바뀐다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addStationInFrontOfUpStation() {
        // when
        Long currentDistance = 5L;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 신사역.getId(), 강남역.getId(), currentDistance);

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        Long distance = response.jsonPath().getLong("distance");
        assertAll(
                () -> assertThat(stationNames).containsExactly(신사역.getName(), 강남역.getName(), 판교역.getName()),
                () -> assertThat(stationNames).hasSize(3),
                () -> assertThat(distance).isEqualTo(lineDistance + currentDistance)
        );
    }

    /**
     * When 노선의 하행 종점을 시작점으로 하는 새로운 역을 생성하면
     * Then 노선에 새로운 구간이 생성된다
     * Then 노선의 하행 종점역이 바뀐다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addStationAfterDownStation() {
        // when
        Long currentDistance = 8L;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 판교역.getId(), 광교역.getId(), currentDistance);

        // then
        List<String> stationNames = response.jsonPath().getList("stations.name", String.class);
        Long distance = response.jsonPath().getLong("distance");
        assertAll(
                () -> assertThat(stationNames).containsExactly(강남역.getName(), 판교역.getName(), 광교역.getName()),
                () -> assertThat(stationNames).hasSize(3),
                () -> assertThat(distance).isEqualTo(lineDistance + currentDistance)
        );
    }

    /**
     * When 노선에 기존 역 사이 거리보다 크거나 같은 거리의 역을 등록하면
     * Then 노선에 새로운 구간이 생성이 안된다
     */
    @DisplayName("기존 역 사이 거리보다 크거나 같은 거리의 역을 노선에 등록한다.")
    @ParameterizedTest
    @ValueSource(longs = {10L, 12L, 25L})
    void addSectionWhichHasEqualOrLongerDistance(Long currentDistance) {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재시민의숲.getId(), currentDistance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 노선에 역들을 생성하고
     * When 노선에 기존에 등록된 역들의 구간을 등록하면
     * Then 노선에 새로운 구간이 생성이 안된다
     */
    @DisplayName("노선에 기등록된 역들의 구간을 등록한다.")
    @Test
    void addSectionDuplicateInLine() {
        // given
        Long currentDistance = 5L;
        지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재시민의숲.getId(), currentDistance);

        // when
        ExtractableResponse<Response> response1 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 판교역.getId(), lineDistance);
        ExtractableResponse<Response> response2 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재시민의숲.getId(), currentDistance);
        ExtractableResponse<Response> response3 = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 양재시민의숲.getId(), 양재시민의숲.getId(), currentDistance);

        // then
        assertAll(
                () -> assertThat(response1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(response3.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    /**
     * When 노선에 등록되지 않은 역들의 구간을 등록하면
     * Then 노선에 새로운 구간이 생성이 안된다
     */
    @DisplayName("노선에 등록되지 않은 역들의 구간을 등록한다.")
    @Test
    void addSectionNotInLine() {
        // when
        Long currentDistance = 7L;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 신사역.getId(), 양재시민의숲.getId(), lineDistance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록_요청(Long lineId, Long upStationId, Long downStationId, Long distance) {
        Map<String, Long> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .pathParam("id", lineId)
                .when().post("/lines/{id}/sections")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
    }
}
