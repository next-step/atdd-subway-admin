package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.DatabaseCleanup;
import nextstep.subway.dto.LineCreateRequest;
import nextstep.subway.dto.LineResponse;
import nextstep.subway.dto.LineUpdateRequest;
import nextstep.subway.dto.StationRequest;
import nextstep.subway.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    private final String LINE_URI = "/lines";
    private final String STATION_URI = "/stations";
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseCleanup databaseCleanup;
    private StationResponse 강남역;
    private StationResponse 판교역;
    private StationResponse 선릉역;
    private StationResponse 청계산역;


    public StationResponse 지하철역_생성_요청(StationRequest stationRequest) {
        return RestAssured.given().log().all()
            .body(stationRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(STATION_URI)
            .then().log().all().extract().as(StationResponse.class);
    }

    public ExtractableResponse<Response> 노선_생성_요청(LineCreateRequest lineCreateRequest) {
        return RestAssured.given().log().all()
            .body(lineCreateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .post(LINE_URI)
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 노선_목록_조회_요청() {
        return RestAssured
            .given().log().all()
            .when()
            .get(LINE_URI)
            .then().log().all()
            .extract();
    }

    ExtractableResponse<Response> 노선_조회_요청(Long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_URI + "/" + id)
            .then().log().all().extract();
    }

    ExtractableResponse<Response> 노선_수정_요청(Long id, LineUpdateRequest lineUpdateRequest) {
        return RestAssured
            .given().log().all()
            .body(lineUpdateRequest)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().put(LINE_URI + "/" + id)
            .then().log().all().extract();
    }

    ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return RestAssured
            .given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().get(LINE_URI + "/" + id)
            .then().log().all().extract();
    }

    void 지하철_노선_응답됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    void 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    void 지하철_노선_삭제됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    StationRequest 강남역_생성_요청값() {
        return new StationRequest("강남역");
    }

    StationRequest 판교역_생성_요청값() {
        return new StationRequest("판교역");
    }

    StationRequest 선릉역_생성_요청값() {
        return new StationRequest("선릉역");
    }

    StationRequest 청계산역_생성_요청값() {
        return new StationRequest("청계산역");
    }


    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
            databaseCleanup.afterPropertiesSet();
        }

        databaseCleanup.execute();

        강남역 = 지하철역_생성_요청(강남역_생성_요청값());
        판교역 = 지하철역_생성_요청(판교역_생성_요청값());
        선릉역 = 지하철역_생성_요청(선릉역_생성_요청값());
        청계산역 = 지하철역_생성_요청(청계산역_생성_요청값());
    }

    /**
     * When 지하철 노선을 생성하면
     * Then 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다.
     */
    @DisplayName("지하철노선을 생성한다.")
    @Test
    void creatLine() {
        //when
        LineCreateRequest 신분당선_생성_요청_값 = new LineCreateRequest("신분당선", "RED", 강남역.getId(), 판교역.getId(), 10);

        ExtractableResponse<Response> response = 노선_생성_요청(신분당선_생성_요청_값);

        //then
        지하철_노선_생성됨(response);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다
     */
    @DisplayName("지하철노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineCreateRequest 신분당선_생성_요청_값 = new LineCreateRequest("신분당선", "RED", 강남역.getId(), 판교역.getId(), 10);
        LineCreateRequest 이호선_생성_요청_값 = new LineCreateRequest("이호선", "GREEN", 강남역.getId(), 선릉역.getId(), 10);
        LineResponse 신분당선_생선됨 = 노선_생성_요청(신분당선_생성_요청_값).as(LineResponse.class);
        LineResponse 이호선_생선됨 = 노선_생성_요청(이호선_생성_요청_값).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 노선_목록_조회_요청();

        //then
        List<Long> 노선_목록 = response.jsonPath()
            .getList(".", LineResponse.class)
            .stream()
            .map(LineResponse::getId)
            .collect(Collectors.toList());

        assertThat(노선_목록.containsAll(Arrays.asList(신분당선_생선됨.getId(), 이호선_생선됨.getId()))).isTrue();
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineCreateRequest 신분당선_생성_요청_값 = new LineCreateRequest("신분당선", "RED", 강남역.getId(), 판교역.getId(), 10);
        LineResponse 신분당선_생선됨 = 노선_생성_요청(신분당선_생성_요청_값).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 노선_조회_요청(신분당선_생선됨.getId());

        // then
        지하철_노선_응답됨(response);
        LineResponse expectResponse = response.as(LineResponse.class);
        assertAll(
            () -> assertThat(expectResponse.getStations()).hasSize(2),
            () -> assertThat(expectResponse.getStations().get(0).getId().equals(강남역.getId())).isTrue(),
            () -> assertThat(expectResponse.getStations().get(1).getId().equals(판교역.getId())).isTrue()
        );
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("지하철노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineCreateRequest 신분당선_생성_요청_값 = new LineCreateRequest("신분당선", "RED", 강남역.getId(), 판교역.getId(), 10);
        LineResponse 신분당선_생선됨 = 노선_생성_요청(신분당선_생성_요청_값).as(LineResponse.class);

        // when
        LineUpdateRequest 신분당선_수정_요청_값 = new LineUpdateRequest("신분당선", "RED", 강남역.getId(), 청계산역.getId(), 10);
        LineResponse response = 노선_수정_요청(신분당선_생선됨.getId(), 신분당선_수정_요청_값).as(LineResponse.class);

        // then
        assertAll(
            () -> assertThat(response.getName()).isEqualTo(신분당선_수정_요청_값.getName())
        );

    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("지하철노선을 삭제한다.")
    @Test
    void deleteLine() {
        // given
        LineCreateRequest 신분당선_생성_요청_값 = new LineCreateRequest("신분당선", "RED", 강남역.getId(), 판교역.getId(), 10);
        LineResponse 신분당선_생선됨 = 노선_생성_요청(신분당선_생성_요청_값).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선_생선됨.getId());

        // then
        지하철_노선_삭제됨(response);
    }
}
