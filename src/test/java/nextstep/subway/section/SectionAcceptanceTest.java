package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("상행선보다 이전역이 추가될경우")
    @Test
    void addSectionBeforeUpStation() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 삼성역, 잠실역, "10");

        // 구간추가요청
        ExtractableResponse<Response> response = 구간_추가_요청(uri, 강남역, 삼성역, "20");

        // 구간생성확인
        구간_생성됨(response);
    }

    @DisplayName("상행선과 하행선 사이에 추가될경우")
    @Test
    void addSectionBetweenUpStationDownStation() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 잠실역, "30");

        // 구간추가요청
        ExtractableResponse<Response> response = 구간_추가_요청(uri, 강남역, 삼성역, "20");

        // 구간생성확인
        구간_생성됨(response);
    }

    // 하행선역 이후 구간이 추가될경우
    @DisplayName("하행선역 이후 구간이 추가될경우")
    @Test
    void addSectionAfterDownStation() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 삼성역, "20");

        // 구간추가요청
        ExtractableResponse<Response> response = 구간_추가_요청(uri, 삼성역, 잠실역, "10");

        // 구간생성확인
        구간_생성됨(response);
    }

    @DisplayName("역사이에 역이 추가될 경우 기존역보다 거리가 크거나 같을때 등록불가")
    @Test
    void addSectionBetweenInvalidUpStationDownStation() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 잠실역, "20");

        // 구간추가요청
        ExtractableResponse<Response> response = 구간_추가_요청(uri, 삼성역, 잠실역, "20");

        // 구간생성실패확인
        구간_생성실패(response);
    }

    // 동일한 역의 구간 등록 불가능
    @DisplayName("동일한 역의 구간 등록 불가능")
    @Test
    void addSectionSameStation() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 잠실역, "20");

        // 구간추가요청
        ExtractableResponse<Response> response = 구간_추가_요청(uri, 강남역, 잠실역, "20");

        // 구간생성실패확인
        구간_생성실패(response);
    }

    @DisplayName("상행과 하행역이 모두 등록안되어있을 경우 등록 불가능")
    @Test
    void addSectionNotregUpStationDownStation() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 종합운동장역 = 지하철_역_생성("종합운동장역");
        String 잠실역 = 지하철_역_생성("잠실역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 잠실역, "20");

        // 구간추가요청
        ExtractableResponse<Response> response = 구간_추가_요청(uri, 삼성역, 종합운동장역, "20");

        // 구간생성실패확인
        구간_생성실패(response);
    }

    @DisplayName("지하철 노선도 구간제거(노선의 끝역)")
    @Test
    void deleteStation() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 종합운동장역 = 지하철_역_생성("종합운동장역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 종합운동장역, "100");
        // 구간추가요청
        구간_추가_요청(uri, 삼성역, 종합운동장역, "20");

        ExtractableResponse<Response> response = 지하철노선_구간_제거_요청(uri, 강남역);

        구간_제거성공(response);
    }

    @DisplayName("지하철 노선도 구간제거(노선 중간역)")
    @Test
    void deleteStationBetweenSection() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");
        String 종합운동장역 = 지하철_역_생성("종합운동장역");
        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 종합운동장역, "100");
        // 구간추가요청
        구간_추가_요청(uri, 삼성역, 종합운동장역, "20");

        ExtractableResponse<Response> response = 지하철노선_구간_제거_요청(uri, 삼성역);

        구간_제거성공(response);
    }

    @DisplayName("지하철 노선도 구간제거 실패")
    @Test
    void deleteStationFail() {
        //given
        String 강남역 = 지하철_역_생성("강남역");
        String 삼성역 = 지하철_역_생성("삼성역");

        // 지하철_노선_생성_요청
        String uri = 지하철_노선_생성_요청("bg-green-600", "2호선", 강남역, 삼성역, "100");

        ExtractableResponse<Response> response = 지하철노선_구간_제거_요청(uri, 삼성역);

        구간_제거실패(response);
    }

    private ExtractableResponse<Response> 구간_추가_요청(String uri, String upStation, String downStation, String distance) {
        Map<String, String> param = createSectionParam(upStation, downStation, distance);
        return post(uri, param);
    }

    private Map<String, String> createSectionParam(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }

    private String 지하철_노선_생성_요청(String lineColor, String lineName, String upStationId, String downStationId, String distance) {
        Map<String, String> params = createLineParam(lineColor, lineName, upStationId, downStationId, distance);
        ExtractableResponse<Response> reponse = post("/lines", params);
        return reponse.header("Location") + "/sections";
    }

    private Map<String, String> createLineParam(String lineColor, String lineName, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", lineColor);
        params.put("name", lineName);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return params;
    }

    private String 지하철_역_생성(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        ExtractableResponse<Response> response = post("/stations", params);
        return response.header("Location").split("/")[2];
    }

    private ExtractableResponse<Response> 지하철노선_구간_제거_요청(String uri, String stationId) {
        uri += "?stationId=" + stationId;
        return delete(uri);
    }

    private void 구간_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 구간_생성실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private void 구간_제거성공(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 구간_제거실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}