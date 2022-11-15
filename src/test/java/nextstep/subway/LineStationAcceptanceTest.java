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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 역 등록 관련 기능")
public class LineStationAcceptanceTest extends BaseAcceptanceTest {

    int 초기_노선_길이;
    Long 노선_ID;
    Long 상행역_ID;
    Long 하행역_ID;
    LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // Given
        초기_노선_길이 = 12;
        신분당선 = LineAcceptanceTest.지하철노선_생성_요청("신분당선", "bg-red-600", "강남역", "광교역", 초기_노선_길이).as(LineResponse.class);
        노선_ID = 신분당선.getId();
        상행역_ID = 신분당선.getStations().get(0).getId(); // TODO: index 접근이 아닌 다른 방법 생각하기
        하행역_ID = 신분당선.getStations().get(1).getId();
    }

    /**
     * Given 지하철 노선에 구간을 등록하고
     * When 노선에 새로운 구간을 등록하면
     * Then 새로운 역이 노선에 포함된다.
     */
    @DisplayName("노선에 구간을 등록한다.")
    @Test
    void 새로운_역_등록() {
        // When
        ExtractableResponse<Response> 신규역 = 지하철역_생성_요청("신규역");
        Long 신규역_ID = 객체_응답_ID(신규역);
        ExtractableResponse<Response> 지하철_노선에_지하철역_등록_응답 = 지하철_노선에_지하철역_생성_요청(노선_ID, 상행역_ID, 신규역_ID, 4);

        // Then
        지하철_노선에_지하철역_등록_확인(지하철_노선에_지하철역_등록_응답, "강남역", "신규역", "광교역");
    }

    // TODO: 메서드명과 display name이 같은 경우는 Display name을 제거하는지?
    @DisplayName("새로운 역을 상행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_상행_종점으로_등록() {
        // When
        ExtractableResponse<Response> 신규역 = 지하철역_생성_요청("신사역");
        Long 신규역_ID = 객체_응답_ID(신규역);
        ExtractableResponse<Response> 지하철_노선에_지하철역_등록_응답 = 지하철_노선에_지하철역_생성_요청(노선_ID, 신규역_ID, 상행역_ID, 3);

        // Then
        지하철_노선에_지하철역_등록_확인(지하철_노선에_지하철역_등록_응답, "신사역", "강남역", "광교역");
    }

    @DisplayName("새로운 역을 하행 종점으로 등록할 경우")
    @Test
    void 새로운_역을_하행_종점으로_등록() {
        // When
        ExtractableResponse<Response> 신규역 = 지하철역_생성_요청("동천역");
        Long 신규역_ID = 객체_응답_ID(신규역);
        ExtractableResponse<Response> 지하철_노선에_지하철역_등록_응답 = 지하철_노선에_지하철역_생성_요청(노선_ID, 신규역_ID, 상행역_ID, 3);

        // Then
        지하철_노선에_지하철역_등록_확인(지하철_노선에_지하철역_등록_응답, "강남역", "광교역", "동천역");
    }

    @DisplayName("예외 테스트 : 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없음")
    @ParameterizedTest
    @ValueSource(ints = {12, 13})
    void 기존_역_사이_길이보다_크거나_같은_길이_등록_예외(int distance) {
        // When
        ExtractableResponse<Response> 신규역 = 지하철역_생성_요청("양재역");
        Long 신규역_ID = 객체_응답_ID(신규역);
        ExtractableResponse<Response> 지하철_노선에_지하철역_등록_응답 = 지하철_노선에_지하철역_생성_요청(노선_ID, 하행역_ID, 신규역_ID, distance);

        // Then
        assertThat(지하철_노선에_지하철역_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("예외 테스트 : 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없음")
    @Test
    void 상행역과_하행역이_이미_노선에_등록_예외() {
        // When
        ExtractableResponse<Response> 신규역 = 지하철역_생성_요청("양재역");
        Long 신규역_ID = 객체_응답_ID(신규역);
        ExtractableResponse<Response> 지하철_노선에_지하철역_등록_응답 = 지하철_노선에_지하철역_생성_요청(노선_ID, 상행역_ID, 하행역_ID, 4);

        // Then
        assertThat(지하철_노선에_지하철역_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("예외 테스트 : 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void 상행역과_하행역이_노선에_모두_존재하지않는_경우_예외() {
        // When
        ExtractableResponse<Response> 신규역1 = 지하철역_생성_요청("없는역1");
        Long 신규역1_ID = 객체_응답_ID(신규역1);
        ExtractableResponse<Response> 신규역2 = 지하철역_생성_요청("없는역2");
        Long 신규역2_ID = 객체_응답_ID(신규역2);
        ExtractableResponse<Response> 지하철_노선에_지하철역_등록_응답 = 지하철_노선에_지하철역_생성_요청(노선_ID, 신규역1_ID, 신규역2_ID, 4);

        // Then
        assertThat(지하철_노선에_지하철역_등록_응답.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 지하철_노선에_지하철역_등록_확인(
                ExtractableResponse<Response> response,
                String upStationsName,
                String downStationName,
                String newStationName) {

        List<String> list = response.jsonPath().getList("stations.name", String.class);
        assertThat(list).contains(upStationsName, downStationName, newStationName);
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
}
