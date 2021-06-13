package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.util.*;
import java.util.stream.Collectors;
import static nextstep.subway.station.StationAcceptanceTest.createStation;
import static nextstep.subway.station.StationAcceptanceTest.createStationParams;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @BeforeEach
    public void setUp() {
        super.setUp();
        지하철_역_생성_요청();
    }
    
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        //when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청("1호선", "blue", 1L, 2L, 7);

        //then
        노선에_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        //given
        지하철_노선_생성_요청("1호선", "blue", 1L, 2L, 7);

        //when
        ExtractableResponse<Response> response =  지하철_노선_생성_요청("1호선", "blue", 1L, 2L, 7);

        //then
        지하철_노선_중복_등록_불가(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        //given
        ExtractableResponse<Response> createResponse1 =  지하철_노선_생성_요청("1호선", "blue", 1L, 2L, 7);
        ExtractableResponse<Response> createResponse2 =  지하철_노선_생성_요청("2호선", "grean", 3L, 4L, 7);

        //when
        ExtractableResponse<Response> response = findAllLines();

        // then
        노선에_역_등록됨(response);
        노선_목록_전체_조회됨(response, createResponse1, createResponse2);
    }

    @DisplayName("지하철 노선과 구간을 조회한다.")
    @Test
    void getLine() {
        //given
        지하철_노선_생성_요청("1호선", "blue", 1L, 2L, 7);

        //when
        ExtractableResponse<Response> response = findLineById(1L);

        //then
        노선에_역_등록됨(response);
        상행역_하행역_순으로_정렬_됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given
        지하철_노선_생성_요청("1호선", "blue", 1L, 2L, 7);

        //when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청("2호선","green");

        //then
        노선에_역_등록됨(response);
        노선에_역_수정됨(response, "2호선", "green");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        //given
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청("1호선", "blue", 1L, 2L, 7);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createResponse);

        // then
        지하철_노선_제거됨(response);
    }

    private static Map<String, String> createParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    private ExtractableResponse<Response> findAllLines() {
        return RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> findLineById(Long id) {
        return RestAssured.given().log().all()
                .when()
                .get("/lines/{id}", id)
                .then().log().all()
                .extract();
    }

    private void 지하철_역_생성_요청() {
        createStation(createStationParams("구로역")).as(StationResponse.class);
        createStation(createStationParams("신도림역")).as(StationResponse.class);
        createStation(createStationParams("영등포역")).as(StationResponse.class);
        createStation(createStationParams("신길역")).as(StationResponse.class);
    }

    public static void 노선에_역_등록됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    public static void 노선에_역_생성됨(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String name, String color) {
       return RestAssured.given().log().all()
                .body(createParams(name, color))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/{id}", 1L)
                .then().log().all()
                .extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(ExtractableResponse<Response> createResponse) {
        String uri = createResponse.header("Location");
        return  RestAssured.given().log().all()
                .when()
                .delete(uri)
                .then().log().all()
                .extract();
    }
    public static void 지하철_노선_중복_등록_불가(ExtractableResponse<Response> response) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    public static void 노선_목록_전체_조회됨(ExtractableResponse<Response> response, ExtractableResponse<Response> createResponse1, ExtractableResponse<Response> createResponse2) {
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    public static void 노선에_역_수정됨(ExtractableResponse<Response> response, String name, String color) {
        assertThat(response.jsonPath().get("name").toString()).isEqualTo(name);
        assertThat(response.jsonPath().get("color").toString()).isEqualTo(color);
    }

    public static void 지하철_노선_제거됨(ExtractableResponse<Response> response){
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static  void 상행역_하행역_순으로_정렬_됨(ExtractableResponse<Response> response) {
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        StationResponse upStation = lineResponse.getStations().get(0);
        StationResponse downStation = lineResponse.getStations().get(lineResponse.getStations().size()-1);

        assertThat(upStation.getName()).isEqualTo("구로역");
        assertThat(downStation.getName()).isEqualTo("신도림역");
    }
}
