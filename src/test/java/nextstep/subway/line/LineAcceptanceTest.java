package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // 지하철_역_생성되어_있음
        createStation("강남역");
        createStation("신도림역");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createSubwayLine(
                "분당선", "yellow", "1", "2","8"
        );

        // then
        // 지하철_노선_생성됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());

        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo("분당선");
        Assertions.assertThat(lineResponse.getColor()).isEqualTo("yellow");
        Assertions.assertThat(lineResponse.getStations())
                .extracting(StationResponse::getName)
                .containsExactly("강남역", "신도림역");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_역_생성되어_있음
        createStation("강남역");
        createStation("신도림역");
        createStation("잠실역");
        // 지하철_노선_등록되어_있음
        createSubwayLine(
                "분당선", "yellow", "1", "2","8"
        );

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createSubwayLine(
                "분당선", "yellow", "1", "3","6"
        );

        // then
        // 지하철_노선_생성_실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_역_생성되어_있음
        createStation("강남역");
        createStation("신도림역");
        createStation("방화역");
        createStation("오금역");
        // 지하철_노선_등록되어_있음
        createSubwayLine(
                "분당선", "yellow", "1", "2","8"
        );
        createSubwayLine(
                "5호선", "purple", "1", "4","4"
        );

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = searchSubwayLineAll();

        // then
        // 지하철_노선_목록_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> lineResponses = response.jsonPath().getList(".", LineResponse.class);
        Assertions.assertThat(lineResponses.size()).isEqualTo(2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_역_생성되어_있음
        createStation("강남역");
        createStation("신도림역");
        createStation("방화역");
        createStation("오금역");
        // 지하철_노선_등록되어_있음
        createSubwayLine(
                "분당선", "yellow", "1", "2","8"
        );
        createSubwayLine(
                "5호선", "purple", "1", "4","4"
        );

        // when
        // 지하철_노선_조회_요청
        Long id = 1L;
        ExtractableResponse<Response> response = searchSubwayLineOne(id);
        ExtractableResponse<Response> response2 = searchSubwayLineOne(2L);

        // then
        // 지하철_노선_응답됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo("분당선");
        Assertions.assertThat(lineResponse.getColor()).isEqualTo("yellow");
        Assertions.assertThat(lineResponse.getStations().get(0).getName()).isEqualTo("강남역");
        LineResponse lineResponse2 = response2.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse2.getStations().get(0).getName()).isEqualTo("강남역");
    }

    @DisplayName("지하철 노선을 조회실패한다.")
    @Test
    void getLine2() {
        // given
        // 지하철_역_생성되어_있음
        createStation("강남역");
        createStation("신도림역");
        // 지하철_노선_등록되어_있음
        createSubwayLine(
                "분당선", "yellow", "1", "2","8"
        );

        // when
        // 지하철_노선_조회_요청
        Long id = 4L;
        ExtractableResponse<Response> response = searchSubwayLineOne(id);

        // then
        // 지하철_노선_응답 실패됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_역_생성되어_있음
        createStation("강남역");
        createStation("신도림역");
        // 지하철_노선_등록되어_있음
        createSubwayLine(
                "분당선", "yellow", "1", "2","8"
        );

        // when
        // 지하철_노선_수정_요청
        Long id = 1L;
        ExtractableResponse<Response> response =
                modifySubwayLine(id, "2호선", "green");

        // then
        // 지하철_노선_수정됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = response.response().getBody().as(LineResponse.class);
        Assertions.assertThat(lineResponse.getName()).isEqualTo("2호선");
        Assertions.assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_역_생성되어_있음
        createStation("강남역");
        createStation("신도림역");
        // 지하철_노선_등록되어_있음
        createSubwayLine(
                "분당선", "yellow", "1", "2","8"
        );

        // when
        // 지하철_노선_제거_요청
        Long id = 1L;
        ExtractableResponse<Response> response = deleteSubwayLine(id);

        // then
        // 지하철_노선_삭제됨
        Assertions.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private void createStation(String station) {
        StationAcceptanceTest.createStation(station);
    }

    public static Map<String, String> createParams(String name, String color,
                                            String upStationId, String downStationId,
                                            String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }
    public Map<String, String> modifyParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }
    public static Map<String, String> addStationParams(String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }


    @DisplayName("지하철 노선 생성 요청")
    public static ExtractableResponse<Response> createSubwayLine(String name, String color,
                                                          String upStationId, String downStationId,
                                                          String distance) {
        Map<String, String> params = createParams(name, color, upStationId, downStationId, distance);
        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines").
                then().
                log().all().
                extract();
    }

    @DisplayName("지하철 노선 조회")
    public static ExtractableResponse<Response> searchSubwayLineOne(Long id) {
        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/lines/" + id).
                then().
                log().all().
                extract();
    }

    @DisplayName("지하철 노선 목록 조회")
    public ExtractableResponse<Response> searchSubwayLineAll() {
        return RestAssured.given().log().all().
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                get("/lines").
                then().
                log().all().
                extract();
    }

    @DisplayName("지하철 노선 수정")
    public ExtractableResponse<Response> modifySubwayLine(Long id, String name, String color) {
        Map<String, String> params = modifyParams(name, color);
        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                put("/lines/" + id).
                then().
                log().all().
                extract();
    }

    @DisplayName("지하철 노선 삭제")
    public ExtractableResponse<Response> deleteSubwayLine(Long id) {
        return RestAssured.given().log().all().
                when().
                delete("/lines/" + id).
                then().
                log().all().
                extract();
    }

    @DisplayName("지하철 구간 추가")
    public static ExtractableResponse<Response> addSubwayStation(Long id, String upStationId, String downStationId, String distance) {
        Map<String, String> params = addStationParams(upStationId, downStationId, distance);
        return RestAssured.given().log().all().
                body(params).
                contentType(MediaType.APPLICATION_JSON_VALUE).
                when().
                post("/lines/" + id + "/sections").
                then().
                log().all().
                extract();
    }
}
