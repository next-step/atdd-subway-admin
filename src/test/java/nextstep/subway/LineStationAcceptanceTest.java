package nextstep.subway;

import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.List;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.line.LineAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 역 등록 관련 기능")
public class LineStationAcceptanceTest extends BaseAcceptanceTest {

    int distance;
    Long 신분당선_ID;
    Long 강남역_ID;
    Long 광교역_ID;
    LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // Given
        distance = 12;
        신분당선 = LineAcceptanceTest.지하철노선_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", distance).as(LineResponse.class);
        신분당선_ID = 신분당선.getId();
        강남역_ID = 신분당선.getStations().get(0).getId(); // TODO: index 접근이 아닌 다른 방법 생각하기
        광교역_ID = 신분당선.getStations().get(1).getId();
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 노선에 새로운 구간을 등록하면
     * Then 새로운 역이 노선에 포함된다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void addSection() {
        // When
        ExtractableResponse<Response> 판교역 = 지하철역_생성_요청("판교역");
        Long 판교역_ID = 객체_응답_ID(판교역);
        ExtractableResponse<Response> 지하철_노선에_지하철역_등록_응답 = 지하철_노선에_지하철역_생성_요청(신분당선_ID, 강남역_ID, 판교역_ID, 4);

        // Then
        지하철_노선에_지하철역_등록_확인(지하철_노선에_지하철역_등록_응답);
    }

    private void 지하철_노선에_지하철역_등록_확인(ExtractableResponse<Response> response) {
        List<String> list = response.jsonPath().getList("stations.name", String.class);
        assertThat(list).contains("강남역", "판교역", "광교역");
        // TODO: 기존 길이 확인.
        // TODO: 변경된 길이 확인.
    }

    private ExtractableResponse<Response> 지하철_노선에_지하철역_생성_요청(Long lineId, Long upStationId, Long downStationId, int distance) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("lineId", lineId);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines/{lineId}/stations", lineId)
                .then().log().all()
                .extract();
    }

    // TODO: 메서드명과 display name이 같은 경우는 Display name을 제거하는지?
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_상행_종점으로_등록() {

    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_하행_종점으로_등록() {
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @Test
    void test3() {
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void test4() {
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void test5() {
    }
}
