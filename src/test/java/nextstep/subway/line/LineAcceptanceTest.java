package nextstep.subway.line;

import static nextstep.subway.station.StationAcceptanceTest.응답코드_확인;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;

@Sql("/truncate.sql")
@DisplayName("지하철 노선 관련 기능")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LineAcceptanceTest {

    @LocalServerPort
    int port;
    private Map<String, String> params;

    @BeforeEach
    public void setUp() {
        if (RestAssured.port == RestAssured.UNDEFINED_PORT) {
            RestAssured.port = port;
        }
        params = new HashMap<>();
    }

    /**
     * When : 지하철 노선을 생성하면 Then : 지하철 노선 목록 조회 시 생성한 노선을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createLine() {
        // given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("판교역");
        // when
        ExtractableResponse<Response> createResponse = 지하철노선_생성_요청("신분당선", "bg-red-600", 1L, 2L,
            10L);
        // then
        응답코드_확인(createResponse, HttpStatus.CREATED);
        ExtractableResponse<Response> getResponse = 지하철노선_목록_조회_요청();
        지하철노선목록_이름_포함_확인(getResponse, Arrays.asList("신분당선"));
    }


    /**
     * Given 2개의 지하철 노선을 생성하고 When 지하철 노선 목록을 조회하면 Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("지하철역 목록 조회한다.")
    @Test
    void getStations() {
        //given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("판교역");
        지하철역_생성_요청("잠실역");
        지하철역_생성_요청("건대역");
        지하철노선_생성_요청("신분당선", "bg-red-600", 1L, 2L,
            10L);
        지하철노선_생성_요청("2호선", "bg-blue-600", 3L, 4L,
            10L);

        //when
        ExtractableResponse<Response> getResponse = 지하철노선_목록_조회_요청();

        //then
        지하철노선목록_이름_포함_확인(getResponse, Arrays.asList("신분당선", "2호선"));
    }

    /**
     * Given 지하철 노선을 생성하고 When 생성한 지하철 노선을 조회하면 Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStation() {
        //given
        지하철역_생성_요청("강남역");
        지하철역_생성_요청("판교역");
        지하철노선_생성_요청("신분당선", "bg-red-600", 1L, 2L,
            10L);

        //when
        ExtractableResponse<Response> getResponse = 지하철노선_조회_요청(1L);

        //then
        지하철노선_이름_포함_확인(getResponse, "신분당선");
    }


    private void 지하철노선목록_이름_포함_확인(ExtractableResponse<Response> getResponse, List<String> names) {
        assertThat(getResponse.jsonPath().getList("name")).hasSameElementsAs(names);
    }

    private void 지하철노선_이름_포함_확인(ExtractableResponse<Response> getResponse, String name) {
        String object = getResponse.jsonPath().getString("name");
        assertThat(object).isEqualTo(name);
    }

    private ExtractableResponse<Response> 지하철노선_목록_조회_요청() {
        return RestAssured.given().log().all()
            .when().get("/lines")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_조회_요청(Long id) {
        return RestAssured.given().log().all()
            .pathParam("id", id)
            .when().get("/lines/{id}")
            .then().log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철노선_생성_요청(String name, String color, Long upStationId,
        Long downStationId, Long distance) {
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", String.valueOf(upStationId));
        params.put("downStationId", String.valueOf(downStationId));
        params.put("distance", String.valueOf(distance));

        return RestAssured.given().log().all()
            .body(params)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when().post("/lines")
            .then().log().all()
            .extract();
    }
}
