package nextstep.subway.subway;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse 강남역;
    StationResponse 정자역;
    LineResponse 신분당선;
    final int 신분당선_노선_길이 = 10;

    private ExtractableResponse<Response> getStations() {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/stations")
                .then().log().all()
                .extract();
    }

    @BeforeEach
    public void setUp() {
        super.setUp();
        강남역 = 지하철역_등록("강남역").as(StationResponse.class);
        정자역 = 지하철역_등록("정자역").as(StationResponse.class);
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", 강남역.getId());
        params.put("downStationId", 정자역.getId());
        params.put("distance", 신분당선_노선_길이);
        신분당선 = 지하철노선_등록(params).as(LineResponse.class);
    }

    protected ExtractableResponse<Response> 지하철구간_등록(Map<String, Object> params, Long lineId) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    protected ExtractableResponse<Response> 지하철구간_제거(Long lineId, Long stationId) {
        return RestAssured.given().log().all()
                //.body(params)
                //.contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().delete("/lines/{lineId}/sections?stationId={stationId}", lineId, stationId)
                .then().log().all()
                .extract();
    }

    /**
     * Given 지하철역과 노선을 등록하고
     * When 노선의 역들 사이에 새로운 역을 등록하면
     * Then 노선에 새로운 구간이 생성된다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다")
    @Test
    void 역_사이에_새로운_역_등록() {
        //when
        Long 판교역id = extractId(지하철역_등록("판교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역.getId());
        params.put("downStationId", 판교역id);
        params.put("distance", 4);
        ExtractableResponse<Response> 지하철구간_등록 = 지하철구간_등록(params, 신분당선.getId());

        //then
        assertThat(지하철구간_등록.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(지하철구간_등록.body().jsonPath().getInt("distance")).isEqualTo(신분당선_노선_길이);
    }

    /**
     * Given 지하철역과 노선을 등록하고
     * When 노선의 상행 종점을 하행 역으로 하는 지하철역을 생성하면
     * Then 노선에 새로운 구간이 생성된다
     * Then 노선의 상행 종점역이 바뀐다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다")
    @Test
    void 새로운_역을_상행_종점으로_등록() {
        //when
        Long 신사역id = extractId(지하철역_등록("신사역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 신사역id);
        params.put("downStationId", 강남역.getId());
        params.put("distance", 5);
        ExtractableResponse<Response> 지하철구간_등록 = 지하철구간_등록(params, 신분당선.getId());
        //then
        assertThat(지하철구간_등록.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(지하철구간_등록.body().jsonPath().getInt("distance")).isEqualTo(신분당선_노선_길이 + 5);
    }

    /**
     * Given 지하철역과 노선을 등록하고
     * When 노선의 하행 종점을 상행 역으로 하는 지하철역을 생성하면
     * Then 노선에 새로운 구간이 생성된다
     * Then 노선의 하행 종점역이 바뀐다
     */
    @DisplayName("새로운 역을 하행 종점으로 등록한다")
    @Test
    void 새로운_역을_하행_종점으로_등록() {
        //when
        Long 광교역id = extractId(지하철역_등록("광교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 정자역.getId());
        params.put("downStationId", 광교역id);
        params.put("distance", 4);
        ExtractableResponse<Response> 지하철구간_등록 = 지하철구간_등록(params, 신분당선.getId());
        //then
        assertThat(지하철구간_등록.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(지하철구간_등록.body().jsonPath().getInt("distance")).isEqualTo(신분당선_노선_길이 + 4);
    }

    /**
     * Given 지하철역과 노선을 등록하고
     * When 역 사이에 새로운 역을 등록할 때 지하철 구간 길이와 같거나 큰 길이의 구간을 추가하면
     * Then 400 badRequest를 리턴한다
     */
    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없다() {
        //when
        Long 광교역id = extractId(지하철역_등록("광교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역.getId());
        params.put("downStationId", 광교역id);
        params.put("distance", 10);
        ExtractableResponse<Response> 지하철구간_등록 = 지하철구간_등록(params, 신분당선.getId());
        //then
        assertThat(지하철구간_등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철역과 노선을 등록하고
     * When 이미 존재하는 구간의 상행역과 하행역을 상행역/하행역으로 갖는 구간을 새로 추가하면
     * Then 400 badRequest를 리턴한다
     */
    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
        //when
        Long 광교역id = extractId(지하철역_등록("광교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역.getId());
        params.put("downStationId", 정자역.getId());
        params.put("distance", 3);
        ExtractableResponse<Response> 지하철구간_등록 = 지하철구간_등록(params, 신분당선.getId());
        //then
        assertThat(지하철구간_등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철역과 노선을 등록하고
     * When 새로 추가할 구간의 상행/하행역이 기존에 등록된 구간 중 상행/하행 모두 포함되어 있지 않다면
     * Then 400 badRequest를 리턴한다
     */
    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
        //when
        Long 광교역id = extractId(지하철역_등록("광교역"));
        Long 미금역id = extractId(지하철역_등록("미금역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 광교역id);
        params.put("downStationId", 미금역id);
        params.put("distance", 3);
        ExtractableResponse<Response> 지하철구간_등록 = 지하철구간_등록(params, 신분당선.getId());
        //then
        assertThat(지하철구간_등록.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    /**
     * Given 지하철역, 노선, 구간을 등록하고
     * When 종점을 제거하면
     * Then 종점이 제거된다
     * Then 노선 길이가 제거된 구간만큼 줄어든다
     */
    @Test
    void 종점_제거() {
        //given
        Long 광교역id = extractId(지하철역_등록("광교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 정자역.getId());
        params.put("downStationId", 광교역id);
        params.put("distance", 10);
        지하철구간_등록(params, 신분당선.getId());
        //when
        ExtractableResponse<Response> 지하철구간_제거 = 지하철구간_제거(신분당선.getId(), 광교역id);
        //then
        ExtractableResponse<Response> 지하철노선_조회 = 지하철노선_조회(신분당선.getId());
        assertAll(() -> assertThat(지하철구간_제거.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철노선_조회.body().jsonPath().getInt("distance")).isEqualTo(신분당선_노선_길이),
                () -> assertThat(extractList(지하철노선_조회, "sections.upStation", String.class)).hasSize(1),
                () -> assertThat(extractString(지하철노선_조회, "sections.upStation[0]")).isEqualTo("강남역"),
                () -> assertThat(extractString(지하철노선_조회, "sections.downStation[0]")).isEqualTo("정자역"));
    }

    /**
     * Given 지하철역, 노선, 구간을 등록하고
     * When 가운데 역을 제거하면
     * Then 가운데 역을 하행역/상행역으로 하는 구간이 제거되고
     * Then 가운데 역을 하행역/상행역으로 하는 구간이 합쳐진다.
     */
    @Test
    void 가운데역_제거() {
        //given
        Long 광교역id = extractId(지하철역_등록("광교역"));
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 정자역.getId());
        params.put("downStationId", 광교역id);
        params.put("distance", 10);
        지하철구간_등록(params, 신분당선.getId());
        //when
        ExtractableResponse<Response> 지하철구간_제거 = 지하철구간_제거(신분당선.getId(), 정자역.getId());
        //then
        ExtractableResponse<Response> 지하철노선_조회 = 지하철노선_조회(신분당선.getId());
        assertAll(() -> assertThat(지하철구간_제거.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(지하철노선_조회.body().jsonPath().getInt("distance")).isEqualTo(신분당선_노선_길이 + 10),
                () -> assertThat(extractList(지하철노선_조회, "sections.upStation", String.class)).hasSize(1),
                () -> assertThat(extractString(지하철노선_조회, "sections.upStation[0]")).isEqualTo("강남역"),
                () -> assertThat(extractString(지하철노선_조회, "sections.downStation[0]")).isEqualTo("광교역")
        );
    }
}
