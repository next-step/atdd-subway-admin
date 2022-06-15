package nextstep.subway.section;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import nextstep.subway.BaseSubwayTest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.SectionRequest;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.line.LineAcceptanceTest;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철노선 구간 기능")
public class SectionAcceptanceTest extends BaseSubwayTest {

    StationResponse 강남역;
    StationResponse 광교역;
    LineResponse 신분당선;


    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = StationAcceptanceTest.지하철_생성("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철_생성("광교역").as(StationResponse.class);
        신분당선 = LineAcceptanceTest.지하철노선_생성(LineRequest.of("신분당선","bg-red-600", 강남역.getId(), 광교역.getId(), 10)).as(LineResponse.class);
    }


    /**
    * Given 새로운 지하철 역을 등록한다.
    * When 지하철 노선에 역과 역 사이에 새로운 역을 추가한다.
    * Then 지하철 노선에 사로운 지하철역이 등록된다.
    */
    @DisplayName("역 사이에 새로운 역을 등록할 경우")
    @Test
    void addSection_between_station() {
        // given
        final StationResponse 판교역 = StationAcceptanceTest.지하철_생성("판교역").as(StationResponse.class);

        // when
        지하철_노선에_지하철역_등록(신분당선.getId(), SectionRequest.of(강남역.getId(), 판교역.getId(), 4));

        // then
        final ExtractableResponse<Response> response = LineAcceptanceTest.지하철노선_조회(신분당선.getId());

        assertThat(response.body().jsonPath().getList("stations.name")).containsExactly(강남역.getName(), 판교역.getName() ,광교역.getName());
    }

    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 새로운 역을 상행 종점으로 추가한다.
     * Then 지하철 노선에 사로운 지하철역이 등록된다.
     */
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void addSection_change_upStation() {
        // given
        final StationResponse 신사역 = StationAcceptanceTest.지하철_생성("신사역").as(StationResponse.class);

        // when
        지하철_노선에_지하철역_등록(신분당선.getId(), SectionRequest.of(신사역.getId(), 강남역.getId(), 3));

        // then
        final ExtractableResponse<Response> response = LineAcceptanceTest.지하철노선_조회(신분당선.getId());

        assertThat(response.body().jsonPath().getList("stations.name")).containsExactly(신사역.getName(), 강남역.getName(), 광교역.getName());
    }

    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 새로운 역을 하행 종점으로 추가한다.
     * Then 지하철 노선에 사로운 지하철역이 등록된다.
     */
    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void addSection_change_downStation() {
        // given
        final StationResponse 동천역 = StationAcceptanceTest.지하철_생성("동천역").as(StationResponse.class);

        // when
        지하철_노선에_지하철역_등록(신분당선.getId(), SectionRequest.of(광교역.getId(), 동천역.getId(), 5));

        // then
        final ExtractableResponse<Response> response = LineAcceptanceTest.지하철노선_조회(신분당선.getId());

        assertThat(response.body().jsonPath().getList("stations.name")).containsExactly(강남역.getName(), 광교역.getName(), 동천역.getName());
    }

    /**
     * Given 새로운 지하철 역을 등록한다.
     * When 새로운 지하철 역을 역과 역 사이에 등록 하는데 길이가 기존보다 크거나 같게 추가한다.
     * Then 지하철 노선에 역이 추가되지 않는다.
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @ParameterizedTest
    @ValueSource(ints = {10, 20})
    void addSection_exception_over_the_distance(int distance) {
        // given
        final StationResponse 판교역 = StationAcceptanceTest.지하철_생성("판교역").as(StationResponse.class);

        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철역_등록 = 지하철_노선에_지하철역_등록(신분당선.getId(),
                SectionRequest.of(판교역.getId(), 광교역.getId(), distance));

        // then
        assertThat(지하철_노선에_지하철역_등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 상행역과 하향역을 추가한다.
     * Then 지하철 노선에 역이 추가되지 않는다.
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void addSection_exception_duplicate() {

        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철역_등록 = 지하철_노선에_지하철역_등록(신분당선.getId(),
                SectionRequest.of(강남역.getId(), 광교역.getId(), 2));

        // then
        assertThat(지하철_노선에_지하철역_등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 기존 상행역 또는 하향역을 빼고 구간을 추가한다.
     * Then 지하철 노선에 역이 추가되지 않는다.
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_exception_null() {

        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철역_등록 = 지하철_노선에_지하철역_등록(신분당선.getId(),
                SectionRequest.of(강남역.getId(), null, 10));

        // then
        assertThat(지하철_노선에_지하철역_등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * When 상행역과 하행역에 같은 역을 추가한다.
     * Then 지하철 노선에 역이 추가되지 않는다.
     */
    @DisplayName("상행역과 하행역이 같은 경우 추가 할 수 없음")
    @Test
    void addSection_exception_equals_stations() {

        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철역_등록 = 지하철_노선에_지하철역_등록(신분당선.getId(),
                ImmutableMap.of("upStationId", 강남역.getId().toString(), "downStationId", 강남역.getId().toString(), "distance", "10"));

        // then
        assertThat(지하철_노선에_지하철역_등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철 노선에 새로운 지하철 역을 등록한다.
     * When 지하철 역을 제거한다.
     * Then 지하철 노선에서 역이 제거 된다.
     */
    @Test
    void deleteStation() {
        // given
        final StationResponse 판교역 = StationAcceptanceTest.지하철_생성("판교역").as(StationResponse.class);
        지하철_노선에_지하철역_등록(신분당선.getId(), SectionRequest.of(강남역.getId(), 판교역.getId(), 4));

        // when
        지하철_노선에_지하철역_제거(신분당선.getId(), 판교역.getId());

        // then
        final ExtractableResponse<Response> 지하철노선_조회 = LineAcceptanceTest.지하철노선_조회(신분당선.getId());
        assertThat(지하철노선_조회.jsonPath().getList("stations.name")).containsExactly(강남역.getName(), 광교역.getName());
    }

    /**
     * Given 지하철 노선에 새로운 지하철 역을 등록한다.
     * When 지하철 노선에 등록되지 않은 역을 제거한다.
     * Then 지하철 역이 제거 되지 않는다..
     */
    @Test
    void deleteStation_not_found_station() {
        // given
        final StationResponse 판교역 = StationAcceptanceTest.지하철_생성("판교역").as(StationResponse.class);
        final StationResponse 미금역 = StationAcceptanceTest.지하철_생성("미금역").as(StationResponse.class);
        지하철_노선에_지하철역_등록(신분당선.getId(), SectionRequest.of(강남역.getId(), 판교역.getId(), 4));

        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철역_제거 = 지하철_노선에_지하철역_제거(신분당선.getId(), 미금역.getId());

        // then
        assertThat(지하철_노선에_지하철역_제거.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
    * When 지하철 역을 제거한다.
    * Then 지하철 역이 제거 되지 않는다.
    */
    @Test
    void deleteStation_exception_last_section() {
        // when
        final ExtractableResponse<Response> 지하철_노선에_지하철역_제거 = 지하철_노선에_지하철역_제거(신분당선.getId(), 광교역.getId());

        // then
        assertThat(지하철_노선에_지하철역_제거.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록(final Long lineId, final SectionRequest request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request, ObjectMapperType.JACKSON_2)
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_등록(final Long lineId, final Map<String, String> request) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .body(request, ObjectMapperType.JACKSON_2)
                .post("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_제거(final Long lineId, final Long stationId) {
        return RestAssured.given().log().all()
                .param("stationId", stationId)
                .when()
                .delete("/lines/{id}/sections", lineId)
                .then().log().all()
                .extract();
    }
}
