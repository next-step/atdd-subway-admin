package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.StationResponse;
import nextstep.subway.station.StationAcceptanceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse 강남역;
    private StationResponse 광교역;
    private Map<String, String> lineCreateParams;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = StationAcceptanceTest.지하철역_생성_요청("강남역").as(StationResponse.class);
        광교역 = StationAcceptanceTest.지하철역_생성_요청("광교역").as(StationResponse.class);

        lineCreateParams = new HashMap<>();
        lineCreateParams.put("name", "신분당선");
        lineCreateParams.put("color", "bg-red-600");
        lineCreateParams.put("upStationId", 강남역.getId() + "");
        lineCreateParams.put("downStationId", 광교역.getId() + "");
        lineCreateParams.put("distance", 10 + "");
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineCreateParams);

        // then
        지하철_노선_생성_확인(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철_노선_생성_요청(lineCreateParams);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineCreateParams);

        // then
        지하철_노선_생성_실패(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선 목록을 조회하면
     * Then 생성한 지하철 노선 목록을 응답받을 수 있다.
     */
    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "구분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 강남역.getId() + "");
        params.put("downStationId", 광교역.getId() + "");
        params.put("distance", 15 + "");

        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청(params);
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(lineCreateParams);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답_정상(response);
        지하철_노선_목록_포함_확인(response, Arrays.asList(createResponse1, createResponse2));
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineCreateParams);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createResponse);

        // then
        지하철_노선_조회_응답_확인(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineCreateParams);

        // when
        Map<String, String> params = new HashMap<>();
        params.put("name", "구분당선");
        params.put("color", "bg-red-600");
        params.put("upStationId", 강남역.getId() + "");
        params.put("downStationId", 광교역.getId() + "");
        params.put("distance", 15 + "");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createResponse, params);

        // then
        지하철_노선_수정_확인(response);
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 지하철 노선을 삭제하면
     * Then 지하철 노선은 없다
     */
    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(lineCreateParams);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createResponse);

        // then
        지하철_노선_삭제_확인(response);
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().post("/lines")
                .then().log().all()
                .extract();
    }

    public ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return RestAssured.given().log().all()
                .when().get("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse response) {
        return RestAssured.given().log().all().
                accept(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/lines/{lineId}", response.getId()).
                then().
                log().all().
                extract();
    }


    public ExtractableResponse<Response> 지하철_노선_조회_요청(ExtractableResponse<Response> response) {
        String uri = response.header(HttpHeaders.LOCATION);

        return RestAssured
                .given().log().all()
                .when().get(uri)
                .then()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(ExtractableResponse<Response> response, Map<String, String> params) {
        String uri = response.header(HttpHeaders.LOCATION);

        return RestAssured.given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(params)
                .when().put(uri)
                .then()
                .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> response) {
        String uri = response.header(HttpHeaders.LOCATION);

        return RestAssured.given().log().all()
                .when().delete(uri)
                .then()
                .extract();
    }

    private void 지하철_노선_생성_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();
    }

    private void 지하철_노선_생성_실패(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    private void 지하철_노선_목록_응답_정상(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_목록_포함_확인(ExtractableResponse<Response> response, List<ExtractableResponse<Response>> createdResponses) {
        List<Long> expectedLineIds = createdResponses.stream()
                .map(it -> Long.parseLong(it.header(HttpHeaders.LOCATION).split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    private void 지하철_노선_조회_응답_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.as(LineResponse.class)).isNotNull();
    }

    private void 지하철_노선_수정_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private void 지하철_노선_삭제_확인(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
