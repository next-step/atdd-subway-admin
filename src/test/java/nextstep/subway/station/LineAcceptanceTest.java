package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.constants.ServiceUrl;
import nextstep.subway.dto.LineModifyRequest;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.util.DatabaseCleaner;
import nextstep.subway.util.RequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LineAcceptanceTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.clear();
    }

    /**
     * Given 두개의 지하철 역이 등록되어 있을 때
     * When 지하철 노선을 생성하면
     * Then 응답에 요청했던 정보가 모두 포함되어 있다.
     */
    @Test
    public void 지하철노선_생성() {
        List<Long> stationIds = 두개의_지하철_역이_등록되어_있음("상행역", "하행역");

        ExtractableResponse<Response> response = 지하철_노선을_생성한다("이름", "bg-red-600", 10L, stationIds);

        응답에_요청했던_정보가_모두_포함되어_있다(response, "이름", "bg-red-600", 10L, stationIds);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @Test
    void 지하철노선_목록_조회() {

        지하철_노선_2개를_생성한다();

        ExtractableResponse<Response> response = 지하철_노선_목록을_조회한다();

        지하철_노선_목록을_2개_조회할_수_있다(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @Test
    void 지하철_노선_조회() {
        ExtractableResponse<Response> response = 지하철_노선을_생성한다();

        ExtractableResponse<Response> queryResponse = 지하철_노선을_조회한다(response);

        생성한_지하철_노선의_정보를_응답받을_수_있다(queryResponse, "노선", "색생", 1000L, "상행역", "하행역");
    }



    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @Test
    void 지하철노선_수정() {
        ExtractableResponse<Response> createResponse = 지하철_노선을_생성한다();

        ExtractableResponse<Response> modifyResponse = 지하철_노선을_수정한다(createResponse, "신규노선명", "신규노선색상");

        해당_지하철_노선_정보는_수정된다(modifyResponse,createResponse, "신규노선명", "신규노선색상");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @Test
    void 지하철노선_삭제() {
        ExtractableResponse<Response> response = 지하철_노선을_생성한다();

        지하철_노선을_삭제한다(response);

        해당_지하철_노선_정보는_삭제된다(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 동일한 이름의 노선을 생성하면
     * Then 요청이 실패한다.
     */
    @Test
    void 지하철노선_중복_생성() {
        지하철_노선을_생성한다();

        ExtractableResponse<Response> response = 지하철_노선을_생성한다("노선", "색생2", 1000L, "다른상행역", "다른하행역");

        요청이_실패한다(response);
    }

    /**
     * When 비정상인 노선 식별자로 삭제하려고 하면
     * Then 요청이 실패한다.
     */
    @Test
    void 노선_삭제_실패() {
        ExtractableResponse<Response> response = 지하철_노선을_삭제한다(-1);

        요청이_실패한다(response);
    }

    /**
     * When 존재하지 않는 노선 식별자로 삭제하려고 하면
     * Then 내용이 없다고 회신됨.
     */
    @Test
    void 노선_삭제_실패2() {
        ExtractableResponse<Response> response = 지하철_노선을_삭제한다(10000);

        내용이_없음(response);
    }

    private void 내용이_없음(ExtractableResponse<Response> response) {
        assertStatus(response,HttpStatus.NO_CONTENT);
    }

    private void 요청이_실패한다(ExtractableResponse<Response> response) {
        assertStatus(response,HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> 지하철_노선을_생성한다() {
        return 지하철_노선을_생성한다("노선", "색생", 1000L, "상행역", "하행역");
    }

    private void 지하철_노선을_삭제한다(ExtractableResponse<Response> response) {
        long 노선_식별자 = response.jsonPath().getLong("id");
        ExtractableResponse<Response> deleteResponse = RequestUtil.deleteRequest(String.format("%s/%s",ServiceUrl.URL_LINES,노선_식별자));
        assertStatus(deleteResponse, HttpStatus.NO_CONTENT);
    }

    private ExtractableResponse<Response> 지하철_노선을_삭제한다(int abnormalLineId) {
        return RequestUtil.deleteRequest(String.format("%s/%s",ServiceUrl.URL_LINES,abnormalLineId));
    }

    private void 해당_지하철_노선_정보는_삭제된다(ExtractableResponse<Response> response) {
        ExtractableResponse<Response> queryResponse = 지하철_노선을_조회한다(response);
        assertStatus(queryResponse, HttpStatus.NO_CONTENT);
    }

    private void 해당_지하철_노선_정보는_수정된다(ExtractableResponse<Response> modifyResponse, ExtractableResponse<Response> response, String 노선명, String 색상) {
        assertStatus(modifyResponse,HttpStatus.OK);
        ExtractableResponse<Response> queryResponse = 지하철_노선을_조회한다(response);
        assertThat(queryResponse.jsonPath().getString("name")).isEqualTo(노선명);
        assertThat(queryResponse.jsonPath().getString("color")).isEqualTo(색상);
    }

    private ExtractableResponse<Response> 지하철_노선을_수정한다(ExtractableResponse<Response> response, String 신규노선명, String 신규노선색상) {
        LineModifyRequest body = LineModifyRequest.of(신규노선명, 신규노선색상);
        long 노선_식별자 = response.jsonPath().getLong("id");
        return RequestUtil.putRequest(String.format("%s/%s",ServiceUrl.URL_LINES,노선_식별자), body);
    }

    private void 생성한_지하철_노선의_정보를_응답받을_수_있다(ExtractableResponse<Response> response, String 신규노선, String 노선색상, long 거리, String 상행역, String 하행역) {
        assertThat(response.jsonPath().getLong("id")).isPositive();
        assertThat(response.jsonPath().getString("name")).isEqualTo(신규노선);
        assertThat(response.jsonPath().getString("color")).isEqualTo(노선색상);
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(거리);
        assertThat(response.jsonPath().getList("stations.name")).contains(상행역, 하행역);
    }

    private ExtractableResponse<Response> 지하철_노선을_조회한다(ExtractableResponse<Response> response) {
        long 노선_식별자 = response.jsonPath().getLong("id");
        return RequestUtil.getRequest(String.format("%s/%s",ServiceUrl.URL_LINES,노선_식별자));
    }

    private ExtractableResponse<Response> 지하철_노선을_생성한다(String 신규노선, String 노선색상, long 거리, String 상행역, String 하행역) {
        List<Long> 신규노선역_식별자 = 두개의_지하철_역이_등록되어_있음(상행역, 하행역);
        return 지하철_노선을_생성한다(신규노선, 노선색상, 거리, 신규노선역_식별자);
    }

    private void 지하철_노선_목록을_2개_조회할_수_있다(ExtractableResponse<Response> response) {
        assertThat(response.jsonPath().getList("id").size()).isEqualTo(2);
    }

    private ExtractableResponse<Response> 지하철_노선_목록을_조회한다() {
        return RequestUtil.getRequest(ServiceUrl.URL_LINES);
    }

    private void 지하철_노선_2개를_생성한다() {
        List<Long> ids = 두개의_지하철_역이_등록되어_있음("상행역1", "하행역1");
        지하철_노선을_생성한다("노선1", "color1", 100L, ids);
        List<Long> ids2 = 두개의_지하철_역이_등록되어_있음("상행역2", "하행역2");
        지하철_노선을_생성한다("노선2", "color2", 100L, ids2);
    }

    private void 응답에_요청했던_정보가_모두_포함되어_있다(ExtractableResponse<Response> response, String 노선이름, String color, long distance, List<Long> stationIds) {
        assertStatus(response,HttpStatus.CREATED);
        assertThat(response.jsonPath().getLong("id")).isPositive();
        assertThat(response.jsonPath().getString("name")).isEqualTo(노선이름);
        assertThat(response.jsonPath().getString("color")).isEqualTo(color);
        assertThat(response.jsonPath().getLong("distance")).isEqualTo(distance);
        assertThat(response.jsonPath().getList("stations.id")).contains(stationIds.get(0).intValue(), stationIds.get(1).intValue());

    }

    private ExtractableResponse<Response> 지하철_노선을_생성한다(String lineName, String lineColor, Long distance, List<Long> stationIds) {
        Long upStationId = stationIds.get(0);
        Long downStationId = stationIds.get(1);
        LineRequest request = new LineRequest(lineName, lineColor, distance, upStationId, downStationId);
        return RequestUtil.postRequest(ServiceUrl.URL_LINES, request);
    }

    private List<Long> 두개의_지하철_역이_등록되어_있음(String upStationName, String downStationName) {
        ExtractableResponse<Response> station1 = 지하철역을_생성한다(upStationName);
        ExtractableResponse<Response> station2 = 지하철역을_생성한다(downStationName);
        return Arrays.asList(station1.jsonPath().getLong("id"), station2.jsonPath().getLong("id"));
    }
}
