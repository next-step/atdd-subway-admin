package nextstep.subway.station;

import static nextstep.subway.station.CreateFactory.지하철노선_등록_요청;
import static nextstep.subway.station.CreateFactory.지하철역_등록_요청;
import static nextstep.subway.station.ReadFactory.지하철노선_조회_요청;
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
                () -> assertThat(stationNames).containsOnly(강남역.getName(), 양재시민의숲.getName(), 판교역.getName()),
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
                () -> assertThat(stationNames).containsOnly(신사역.getName(), 강남역.getName(), 판교역.getName()),
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
                () -> assertThat(stationNames).containsOnly(강남역.getName(), 판교역.getName(), 광교역.getName()),
                () -> assertThat(stationNames).hasSize(3),
                () -> assertThat(distance).isEqualTo(lineDistance + currentDistance)
        );
    }

    /**
     * When 노선에 기존 역 사이 거리보다 크거나 같은 거리의 역을 등록하면
     * Then 노선에 새로운 구간이 생성이 안된다
     */
    @DisplayName("기존 역 사이 거리보다 크거나 같은 거리의 역을 노선에 등록한다.")
    @ParameterizedTest(name = "기존 역 사이 거리(10)보다 {0}은 크거나 같으므로 구간이 생성되지 않는다.")
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
    @DisplayName("노선에 기등록된 역들의 구간을 등록하면 새로운 구간이 생성되지 않는다.")
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
    @DisplayName("노선에 등록되지 않은 역들의 구간을 등록하면 새로운 구간이 생성되지 않는다.")
    @Test
    void addSectionNotInLine() {
        // when
        Long currentDistance = 7L;
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(신분당선.getId(), 신사역.getId(), 양재시민의숲.getId(), currentDistance);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }


    /**
     * Given 노선 내 3개 이상 역들을 등록하고
     * When 노선 내 역들 중 상행/하행 종점이 아닌 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않는다
     */
    @DisplayName("노선 내 상행/하행 종점이 아닌 역을 제거하면 노선에서 해당 역이 제거된다.")
    @Test
    void deleteStationInMiddle() {
        // given
        지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재시민의숲.getId(), 4L);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철역_삭제_요청(신분당선.getId(), 양재시민의숲.getId());

        // then
        ExtractableResponse<Response> findResponse = 지하철노선_조회_요청(신분당선.getId());
        List<String> stationNames = findResponse.jsonPath().getList("stations.name", String.class);
        Long distance = findResponse.jsonPath().getLong("distance");
        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(stationNames).hasSize(2),
                () -> assertThat(stationNames).doesNotContain(양재시민의숲.getName()),
                () -> assertThat(distance).isEqualTo(lineDistance)
        );
    }

    /**
     * Given 노선 내 3개 이상 역들을 등록하고
     * When 노선의 상행 종점 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않는다
     * Then 노선의 상행 종점이 바뀐다(노선에 전체 길이가 기존보다 줄어든다)
     */
    @DisplayName("노선의 상행 종점을 제거하면 노선에서 해당 역이 제거되고 상행 종점이 바뀐다.")
    @Test
    void deleteStationWhichIsUpStation() {
        // given
        Long currentDistance = 4L;
        지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재시민의숲.getId(), currentDistance);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철역_삭제_요청(신분당선.getId(), 강남역.getId());

        // then
        ExtractableResponse<Response> findResponse = 지하철노선_조회_요청(신분당선.getId());
        List<String> stationNames = findResponse.jsonPath().getList("stations.name", String.class);
        Long distance = findResponse.jsonPath().getLong("distance");
        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(stationNames).hasSize(2),
                () -> assertThat(stationNames).doesNotContain(강남역.getName()),
                () -> assertThat(distance).isEqualTo(lineDistance - currentDistance)
        );
    }

    /**
     * Given 노선 내 3개 이상 역들을 등록하고
     * When 노선의 하행 종점 역을 제거하면
     * Then 노선 내에 제거된 역이 존재하지 않는다
     * Then 노선의 하행 종점이 바뀐다(노선에 전체 길이가 기존보다 줄어든다)
     */
    @DisplayName("노선의 하행 종점을 제거하면 노선에서 해당 역이 제거되고 하행 종점이 바뀐다.")
    @Test
    void deleteStationWhichIsDownStation() {
        // given
        Long currentDistance = 4L;
        지하철_노선에_지하철역_등록_요청(신분당선.getId(), 강남역.getId(), 양재시민의숲.getId(), currentDistance);

        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철역_삭제_요청(신분당선.getId(), 판교역.getId());

        // then
        ExtractableResponse<Response> findResponse = 지하철노선_조회_요청(신분당선.getId());
        List<String> stationNames = findResponse.jsonPath().getList("stations.name", String.class);
        Long distance = findResponse.jsonPath().getLong("distance");
        assertAll(
                () -> assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value()),
                () -> assertThat(stationNames).hasSize(2),
                () -> assertThat(stationNames).doesNotContain(판교역.getName()),
                () -> assertThat(distance).isEqualTo(lineDistance - currentDistance)
        );
    }

    /**
     * Given 노선 내에 존재하는 구간이 1개이고
     * When 노선에 존재하는 역을 제거하면
     * Then 노선에서 해당 역이 제거되지 않는다
     */
    @DisplayName("노선에 구간이 1개이면 해당 구간에 포함된 역을 노선에서 제거할 수 없다.")
    @Test
    void deleteStationWhenLineHasOneSection() {
        // when
        ExtractableResponse<Response> deleteResponse1 = 지하철_노선에_지하철역_삭제_요청(신분당선.getId(), 판교역.getId());
        ExtractableResponse<Response> deleteResponse2 = 지하철_노선에_지하철역_삭제_요청(신분당선.getId(), 강남역.getId());

        // then
        assertAll(
                () -> assertThat(deleteResponse1.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value()),
                () -> assertThat(deleteResponse2.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value())
        );
    }

    /**
     * When 노선에 존재하지 않는 역을 제거하면
     * Then 노선에서 해당 역이 제거되지 않는다
     */
    @DisplayName("노선에 등록되지 않은 역을 제거하면 노선에서 역이 제거되지 않는다.")
    @Test
    void deleteStationNotInLine() {
        // when
        ExtractableResponse<Response> deleteResponse = 지하철_노선에_지하철역_삭제_요청(신분당선.getId(), 신사역.getId());

        // then
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
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
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_삭제_요청(Long lineId, Long stationId) {
        Map<String, Long> params = new HashMap<>();
        params.put("stationId", stationId);

        return RestAssured.given().log().all()
                .params(params)
                .pathParam("id", lineId)
                .when().delete("/lines/{id}/sections")
                .then().log().all()
                .extract();
    }
}
