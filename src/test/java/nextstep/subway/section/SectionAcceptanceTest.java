package nextstep.subway.section;

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

@DisplayName("구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse 강남역;
    StationResponse 광교역;
    LineResponse 신분당선;

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
        광교역 = 지하철역_등록("광교역").as(StationResponse.class);
        Map<String, Object> params = new HashMap<>();
        params.put("name", "2호선");
        params.put("color", "bg-green-600");
        params.put("upStationId", 강남역.getId());
        params.put("downStationId", 광교역.getId());
        params.put("distance", 10);
        신분당선 = 지하철노선_등록(params).as(LineResponse.class);
    }

    protected ExtractableResponse<Response> 지하철구간_등록(Map<String, Object> params, Long lineId) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/{lineId}/sections", lineId)
                .then().log().all()
                .extract();
    }

    /**
     * Given 지하철역과 노선을 등록하고
     * When 구간을 등록하면
     * Then 노선에 지하철역이 등록된다
     */
    @DisplayName("역 사이에 새로운 역을 등록한다")
    @Test
    void 역_사이에_새로운_역_등록() {
        //given
        Long 판교역id = getId(지하철역_등록("판교역"));
        //when
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", 강남역.getId());
        params.put("downStationId", 판교역id);
        params.put("distance", 4);
        ExtractableResponse<Response> 지하철구간_등록 = 지하철구간_등록(params, 신분당선.getId());

        //then
        assertThat(지하철구간_등록.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * Given 지하철역을 생성하고
     * When 기존에 존재하는 지하철역 이름으로 지하철역을 생성하면
     * Then 지하철역 생성이 안된다
     */
    @DisplayName("새로운 역을 상행 종점으로 등록한다")
    @Test
    void 새로운_역을_상행_종점으로_등록() {

    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다")
    @Test
    void 새로운_역을_하행_종점으로_등록() {
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void 역_사이에_새로운_역을_등록할_경우_기존_역_사이_길이보다_크거나_같으면_등록을_할_수_없다() {
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 상행역과_하행역이_이미_노선에_모두_등록되어_있다면_추가할_수_없음() {
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 행역과_하행역_둘_중_하나도_포함되어있지_않으면_추가할_수_없음() {
    }
}
