package nextstep.subway.station;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.constants.ServiceUrl;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
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
import java.util.HashMap;

import static nextstep.subway.station.LineAcceptanceTest.지하철_노선을_조회한다;
import static nextstep.subway.station.StationAcceptanceTest.assertStatus;
import static nextstep.subway.station.StationAcceptanceTest.지하철역을_생성한다;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SectionAcceptanceTest {
    @LocalServerPort
    int port;

    private StationResponse 상행역;
    private StationResponse 하행역;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        databaseCleaner.clear();

        상행역 = 지하철역을_생성한다("상행역").as(StationResponse.class);
        하행역 = 지하철역을_생성한다("하행역").as(StationResponse.class);
    }

    /**
     * Given 노선이 등록되어 있을 때
     * When 상행역에서 시작한 연결된 구간을 추가하면
     * Then 구간이 생성된다.
     */
    @Test
    void 상행선에서_시작한_내부_구간_추가_정상() {

        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response = 상행역에서_시작하는_구간을_추가한다(lineResponse.getId(), 상행역, "신규역", 5L);

        추가된_구간을_확인할_수_있다(response);
    }

    /**
     * Given 노선이 등록되어 있을 때
     * When 하행역에서 끝나는 연결된 구간을 추가하면
     * Then 구간이 생성된다.
     */
    @Test
    void 하행선에서_끝나는_내부_구간_추가_정상() {

        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response = 하행역에서_끝나는_구간을_추가한다(lineResponse.getId(), 하행역, "신규역", 5L);

        추가된_구간을_확인할_수_있다(response);
    }

    /**
     * Given 노선이 등록되어 있을 때
     * When 하행역에서 끝나면서 노선과 거리가 같은 구간을 추가하면
     * Then 구간_생성을_실패한다.
     */
    @Test
    void 하행선에서_끝나지만_거리가_부적절한_경우() {

        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response = 하행역에서_끝나는_구간을_추가한다(lineResponse.getId(), 하행역, "신규역", 10L);

        구간_생성을_실패한다(response);
    }

    /**
     * Given 노선이 등록되어 있을 때
     * When 상행역에서 끝나는 외부 구간이 추가될 때
     * Then 노선이_연장된다.
     */
    @Test
    void 상행선에서_끝나는_외부_구간_추가() {

        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response = 상행선에서_끝나는_외부_구간_추가(lineResponse.getId(), 상행역, "신규역", 10L);

        노선이_연장된다(lineResponse,response,"신규역","하행역");
    }

    /**
     * Given 노선이 등록되어 있을 때
     * When 하행역에서 시작하는 외부 구간이 추가될 때
     * Then 노선이_연장된다.
     */
    @Test
    void 하행선에서_시작하는_외부_구간_추가() {

        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response = 하행선에서_시작하는_외부_구간_추가(lineResponse.getId(), 하행역, "신규역", 10L);

        노선이_연장된다(lineResponse,response,"상행역","신규역");
    }

    /**
     * Given 노선이 등록되어 있고
     * When  신규 구간의 상행선,하행선이 노선과 동일하면
     * Then  구간 생성이 실패함
     */
    @Test
    void 상행역_하행역이_노선과_동일() {
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response = 상행역_하행역이_노선과_동일한_구간을_생성함(lineResponse.getId(), 상행역.getId(),하행역.getId(),5L);

        구간_생성을_실패한다(response);
    }

    /**
     * Given 노선이 등록되어 있고
     * When  신규 구간의 상행선,하행선이 노선과 동일하면
     * Then  구간 생성이 실패함
     */
    @Test
    void 상행역_하행역이_노선과_정반대_경우() {
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response = 상행역_하행역이_정반대인_구간을_생성함(lineResponse.getId(), 하행역.getId(),상행역.getId(),5L);

        구간_생성을_실패한다(response);
    }

    /**
     * Given 노선이 등록되어 있고
     * When  신규 구간의 상행선,하행선이 노선의 상행선 혹은 하행선을 포함하지 않으면
     * Then  구간 생성이 실패함
     */
    @Test
    void 상행역_하행역을_둘중_하나도_포함하지_않으면_생성_불가() {
        LineResponse lineResponse = LineAcceptanceTest.지하철_노선을_생성한다("노선", "색상", 10L, Arrays.asList(상행역.getId(), 하행역.getId())).as(LineResponse.class);

        ExtractableResponse<Response> response =  상행역_하행역을_포함하지_않는_구간_생성을_요청함(lineResponse.getId(),"신규역1","신규역2",5L);

        구간_생성을_실패한다(response);
    }

    private ExtractableResponse<Response> 상행역_하행역을_포함하지_않는_구간_생성을_요청함(Long lineId, String 신규역1, String 신규역2, long 구간거리) {
        StationResponse newStation1 = 지하철역을_생성한다(신규역1).as(StationResponse.class);
        StationResponse newStation2 = 지하철역을_생성한다(신규역2).as(StationResponse.class);
        return 구간생성을_요청한다(lineId, newStation1.getId(), newStation2.getId(), 구간거리);
    }

    private ExtractableResponse<Response> 상행역_하행역이_정반대인_구간을_생성함(Long lineId, Long 하행역_식별자, Long 상행역_식별자 , long 구간거리) {
        return 구간생성을_요청한다(lineId, 하행역_식별자, 상행역_식별자, 구간거리);
    }

    private ExtractableResponse<Response> 상행역_하행역이_노선과_동일한_구간을_생성함(Long lineId, Long 상행역_식별자, Long 하행역_식별자, long 구간거리) {
        return 구간생성을_요청한다(lineId, 상행역_식별자, 하행역_식별자, 구간거리);
    }

    public static ExtractableResponse<Response> 구간생성을_요청한다(Long lineId, Long 상행역_식별자, Long 하행역_식별자, long 구간거리) {
        HashMap<Object, Object> params = new HashMap<>();
        params.put("downStationId", 하행역_식별자);
        params.put("upStationId", 상행역_식별자);
        params.put("distance", 구간거리);
        return RequestUtil.postRequest(String.format(ServiceUrl.URL_SECTIONS, lineId), params);
    }

    private ExtractableResponse<Response> 하행선에서_시작하는_외부_구간_추가(Long lineId, StationResponse 하행역, String 신규역, long distance) {
        StationResponse response = 지하철역을_생성한다(신규역).as(StationResponse.class);
        return 구간생성을_요청한다(lineId, 하행역.getId(), response.getId(), distance);
    }

    private void 노선이_연장된다(LineResponse lineResponse,ExtractableResponse<Response> response, String 상행역, String 하행역) {
        assertStatus(response,HttpStatus.CREATED);
        ExtractableResponse<Response> line = 지하철_노선을_조회한다(lineResponse.getId());
        assertThat(line.jsonPath().getList("stations.name")).contains(상행역,하행역);
    }

    private ExtractableResponse<Response> 상행선에서_끝나는_외부_구간_추가(Long lineId, StationResponse 상행역, String 신규역, long distance) {
        StationResponse response = 지하철역을_생성한다(신규역).as(StationResponse.class);
        return 구간생성을_요청한다(lineId, response.getId(), 상행역.getId(), distance);
    }

    private void 구간_생성을_실패한다(ExtractableResponse<Response> response) {
        assertStatus(response,HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> 하행역에서_끝나는_구간을_추가한다(Long lineId, StationResponse 하행역, String 신규역, long distance) {
        StationResponse response = 지하철역을_생성한다(신규역).as(StationResponse.class);
        return 구간생성을_요청한다(lineId, response.getId(), 하행역.getId(), distance);
    }

    private void 추가된_구간을_확인할_수_있다(ExtractableResponse<Response> response) {
        assertStatus(response, HttpStatus.CREATED);
    }

    public static ExtractableResponse<Response> 상행역에서_시작하는_구간을_추가한다(Long lineId, StationResponse 상행역, String 신규역, long distance) {
        StationResponse response = 지하철역을_생성한다(신규역).as(StationResponse.class);
        return 구간생성을_요청한다(lineId, 상행역.getId(), response.getId(), distance);
    }
}
