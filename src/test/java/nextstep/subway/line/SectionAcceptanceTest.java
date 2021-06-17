package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 양재역;
    private StationResponse 정자역;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given: 양재역, 정자역이 등록되어 있음
        양재역 = 지하철역이_등록되어_있음("양재역");
        정자역 = 지하철역이_등록되어_있음("정자역");

        // given: 양재역, 정자역 구간이 포함된 신분당선 노선이 등록되어 있음
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", 양재역.getId() + "");
        params.put("downStationId", 정자역.getId() + "");
        params.put("distance", "20");
        신분당선 = 지하철_노선이_등록되어_있음(params);
    }

    @Test
    void 구간_사이에_새로운_역을_등록() {
        // given: 판교역이 등록되어 있음.
        StationResponse 판교역 = 지하철역이_등록되어_있음("판교역");
        Map<String, String> params = 신규_구간_생성_파라미터(양재역.getId(), 판교역.getId(), 4);

        // when: 양재역, 판교역 구간 등록을 요청한다.
        ExtractableResponse<Response> response = 신규_구간_등록(params, 신분당선.getId());

        // then: 양재역, 판교역 구간이 등록된다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then: 신분당선 노선 지하철역은 양재역, 판교역, 정자역이다.
        List<String> stationNames = 신분당선_노선_조회(신분당선.getId())
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly("양재역", "판교역", "정자역");
    }

    @Test
    void 새로운역을_상행_종점으로_등록() {
        // given: 강남역이 등록되어 있음.
        StationResponse 강남역 = 지하철역이_등록되어_있음("강남역");
        Map<String, String> params = 신규_구간_생성_파라미터(강남역.getId(), 양재역.getId(), 3);

        // when: 강남역, 양재역 구간 등록을 요청한다.
        ExtractableResponse<Response> response = 신규_구간_등록(params, 신분당선.getId());

        // then: 강남역, 양재역 구간이 등록된다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then: 신분당선 노선 지하철역은 강남역, 양재역, 정자역이다.
        List<String> stationNames = 신분당선_노선_조회(신분당선.getId())
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly("강남역", "양재역", "정자역");

    }

    @Test
    void 새로운_역을_하행_종점으로_등록() {
        // given: 미금역이 등록되어 있음.
        StationResponse 미금역 = 지하철역이_등록되어_있음("미금역");
        Map<String, String> params = 신규_구간_생성_파라미터(정자역.getId(), 미금역.getId(), 4);

        // when: 정자역, 미금역이 구간 등록을 요청한다.
        ExtractableResponse<Response> response = 신규_구간_등록(params, 신분당선.getId());

        // then: 정자역, 미금역 구간이 등록된다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        // then: 신분당선 노선 지하철역은 양재역, 정자역, 미금역이다.
        List<String> stationNames = 신분당선_노선_조회(신분당선.getId())
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly("양재역", "정자역", "미금역");
    }

    @Test
    void 구간_사이에_새로운_역을_등록_시_구간거리가_기존_역사이의_구간_거리와_같은_값으로_역_등록() {
        // given: 판교역이 등록되어 있음.
        StationResponse 판교역 = 지하철역이_등록되어_있음("판교역");

        // when: 양재역, 판교역 구간 등록을 요청한다. 등록 시 구간 거리를 양재역, 정자역 구간 거리와 같은 값을 입력한다.
        Map<String, String> params = 신규_구간_생성_파라미터(양재역.getId(), 판교역.getId(), 20);
        ExtractableResponse<Response> response = 신규_구간_등록(params, 신분당선.getId());

        // then: 양재역, 판교역 구간 등록이 실패한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    void 구간_사이에_새로운_역을_등록_시_구간거리가_기존_역사이의_구간_거리보다_큰값으로_역_등록() {
        // given: 판교역이 등록되어 있음.
        StationResponse 판교역 = 지하철역이_등록되어_있음("판교역");

        // when: 양재역, 판교역 구간 등록을 요청한다. 등록 시 구간 거리를 양재역, 정자역 구간 거리보다 큰값을 입력한다.
        Map<String, String> params = 신규_구간_생성_파라미터(양재역.getId(), 판교역.getId(), 21);
        ExtractableResponse<Response> response = 신규_구간_등록(params, 신분당선.getId());

        // then: 양재역, 판교역 구간 등록이 실패한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 상행역과_하행역이_이미_구간에_등록되어_있는_구간_등록() {
        // when: 양재역, 정자역 구간 등록을 요청한다.
        Map<String, String> params = 신규_구간_생성_파라미터(양재역.getId(), 정자역.getId(), 10);
        ExtractableResponse<Response> response = 신규_구간_등록(params, 신분당선.getId());

        // then: 양재역, 정자역 구간 등록이 실패한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 노선에_상행역과_하행역_둘중_하나도_포함되어_있지_않은_구간_등록() {
        // give: 교대역이 등록되어 있음.
        StationResponse 교대역 = 지하철역이_등록되어_있음("교대역");
        // give: 매봉역이 등록되어 있음.
        StationResponse 매봉역 = 지하철역이_등록되어_있음("매봉역");

        Map<String, String> params = 신규_구간_생성_파라미터(교대역.getId(), 매봉역.getId(), 4);
        // when: 교대역, 매봉역 구간 등록을 요청한다.
        ExtractableResponse<Response> response = 신규_구간_등록(params, 신분당선.getId());
        // then: 교대역, 매봉역 구간 등록이 실패한다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 구간_제거_구간_사이의_역_제거() {
        // given: 양재역, 판교역, 정자역이 등록되어 있음
        // given: 양재역-판교역(구간거리: 10), 판교역-정자역(구간거리: 10) 구간이 포함된 신분당선 노선이 등록되어 있음
        Long 판교역_아이디 = 양재역_판교역_정자역_지하철_노선_생성();

        // when: 판교역 구간을 제거 요청 한다
        ExtractableResponse<Response> response = 구간_제거(신분당선.getId(), 판교역_아이디);

        // then: 판교역 구간이 제거된다
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // then: 신분당선 노선 지하철역은 양재역, 정자역이다.
        List<String> stationNames = 신분당선_노선_조회(신분당선.getId())
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly("양재역", "정자역");
    }

    @Test
    void 구간_제거_상행_종점_제거() {
        // given: 양재역, 판교역, 정자역이 등록되어 있음
        // given: 양재역-판교역(구간거리: 10), 판교역-정자역(구간거리: 10) 구간이 포함된 신분당선 노선이 등록되어 있음
        양재역_판교역_정자역_지하철_노선_생성();

        // when: 양재역 구간을 제거 요청 한다
        ExtractableResponse<Response> response = 구간_제거(신분당선.getId(), 양재역.getId());

        // then: 양재역 구간이 제거된다
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // then: 신분당선 노선 지하철역은 판교역, 정자역이다.
        List<String> stationNames = 신분당선_노선_조회(신분당선.getId())
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly("판교역", "정자역");
    }

    @Test
    void 구간_제거_하행_종점_제거() {
        // given: 양재역, 판교역, 정자역이 등록되어 있음
        // given: 양재역-판교역(구간거리: 10), 판교역-정자역(구간거리: 10) 구간이 포함된 신분당선 노선이 등록되어 있음
        양재역_판교역_정자역_지하철_노선_생성();

        // when: 정자역 구간을 제거 요청 한다
        ExtractableResponse<Response> response = 구간_제거(신분당선.getId(), 정자역.getId());

        // then: 정자역 구간이 제거 된다
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // then: 신분당선 노선 지하철역은 판교역, 정자역이다.
        List<String> stationNames = 신분당선_노선_조회(신분당선.getId())
                .getStations().stream().map(StationResponse::getName).collect(Collectors.toList());
        assertThat(stationNames).containsExactly("양재역", "판교역");
    }

    @Test
    void 구간_제거_예외_존재하지_않는_노선의_구간을_삭제_요청() {
        // given: 양재역, 판교역, 정자역이 등록되어 있음
        // given: 양재역-판교역(구간거리: 10), 판교역-정자역(구간거리: 10) 구간이 포함된 신분당선 노선이 등록되어 있음
        양재역_판교역_정자역_지하철_노선_생성();

        // when: 정자역 구간을 제거 요청 한다
        ExtractableResponse<Response> response = 구간_제거(99L, 양재역.getId());

        // then: 양재역 구간 제거가 실패한다
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 구간_제거_예외_노선에_포함되어_있지_않은_지하철역_구간을_삭제_요청() {
        // given: 양재역, 판교역, 정자역이 등록되어 있음
        // given: 양재역-판교역(구간거리: 10), 판교역-정자역(구간거리: 10) 구간이 포함된 신분당선 노선이 등록되어 있음
        양재역_판교역_정자역_지하철_노선_생성();

        // given: 고속버스터미널역이 등록되어 있다
        StationResponse 고속버스터미널역 = 지하철역이_등록되어_있음("고속버스터미널역");

        // when: 고속버스터미널역 구간을 제거 요청 한다
        ExtractableResponse<Response> response = 구간_제거(신분당선.getId(), 고속버스터미널역.getId());

        // then: 고속버스터미널역 구간 제거가 실패한다
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    void 구간_제거_예외_구간이_하나만_존재하는_구간의_지하철역_구간을_삭제_요청() {
        // given: 양재역, 정자역이 등록되어 있음
        // when: 양재역 구간을 제거 요청 한다
        ExtractableResponse<Response> response = 구간_제거(신분당선.getId(), 양재역.getId());

        // then: 양재역 구간 삭제가 실패한다
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    private ExtractableResponse<Response> 구간_제거(Long 노선_아이디, Long 지하철역_아이디) {
        return RestAssured.given().log().all()
                .when()
                .delete(String.format("/lines/%s/sections?stationId=%s", 노선_아이디, 지하철역_아이디))
                .then().log().all()
                .extract();
    }

    private Long 양재역_판교역_정자역_지하철_노선_생성() {
        StationResponse 판교역 = 지하철역이_등록되어_있음("판교역");
        Map<String, String> params = 신규_구간_생성_파라미터(양재역.getId(), 판교역.getId(), 10);
        신규_구간_등록(params, 신분당선.getId());
        return 판교역.getId();
    }

    private ExtractableResponse<Response> 신규_구간_등록(Map<String, String> params, Long 신분당선_노선_아이디) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(String.format("lines/%s/sections", 신분당선_노선_아이디))
                .then().log().all()
                .extract();
    }

    private Map<String, String> 신규_구간_생성_파라미터(Long 상행역_아이디, Long 하행역_아이디, int 구간_길이) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", 상행역_아이디 + "");
        params.put("downStationId", 하행역_아이디 + "");
        params.put("distance", 구간_길이 + "");
        return params;
    }

    private LineResponse 지하철_노선이_등록되어_있음(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract().as(LineResponse.class);
    }

    private StationResponse 지하철역이_등록되어_있음(String name) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return response.as(StationResponse.class);
    }

    private LineResponse 신분당선_노선_조회(Long 신분당선_노선_아이디) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/" + 신분당선_노선_아이디)
                .then().log().all()
                .extract()
                .as(LineResponse.class);
    }

}
