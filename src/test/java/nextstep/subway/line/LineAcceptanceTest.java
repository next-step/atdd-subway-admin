package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineRequest line1;
    private LineRequest line7;
    private StationResponse station1;
    private StationResponse station2;
    private StationResponse station3;
    private StationResponse station4;

    @BeforeEach
    public void setUp() {
        super.setUp();

        station1 = StationAcceptanceTest.지하철역_생성("소요산역").as(StationResponse.class);
        station2 = StationAcceptanceTest.지하철역_생성("인천역").as(StationResponse.class);
        line1 = 지하철_노선_생성_파라미터("1호선", "blue", station1.getId(), station2.getId(), 10);

        station3 = StationAcceptanceTest.지하철역_생성("부평구청역").as(StationResponse.class);
        station4 = StationAcceptanceTest.지하철역_생성("장암역").as(StationResponse.class);
        line7 = 지하철_노선_생성_파라미터("7호선", "khaki", station3.getId(), station4.getId(), 10);

    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(line1);
        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_생성_요청(line1);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(line1);
        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        List<LineResponse> createdResponses = Arrays.asList(지하철_노선_응답_객체_추출(지하철_노선_생성_요청(line1)),
            지하철_노선_응답_객체_추출(지하철_노선_생성_요청(line7)));
        List<Long> expectedIds = 지하철_노선_응답_객체_아이디_추출(createdResponses);
        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();
        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        Assertions.assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            LineResponse[] lineResponses = response.as(LineResponse[].class);
            for (LineResponse lineResponse : lineResponses) {
                LineResponse createdResponse = createdResponses.stream()
                    .filter(lineResponse1 -> lineResponse1.getId().equals(lineResponse.getId()))
                    .findFirst().get();
                List<Long> collect = lineResponse.getStations().stream()
                    .map(StationResponse::getId)
                    .collect(Collectors.toList());
                List<Long> collect2 = createdResponse.getStations().stream()
                    .map(StationResponse::getId)
                    .collect(Collectors.toList());
                assertThat(collect).containsAll(collect2);
            }

        });
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(line1);
        LineResponse createdLineResponse = 지하철_노선_응답_객체_추출(createdResponse);
        String locationHeader = 지하철_노선_생성_요청_LOCATION_헤더_추출(createdResponse);
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(locationHeader);
        // then
        // 지하철_노선_응답됨
        Assertions.assertAll(() -> {
            assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.as(LineResponse.class).getId())
                .isEqualTo(createdLineResponse.getId());
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(line1);
        LineResponse createdLineResponse = 지하철_노선_응답_객체_추출(createdResponse);
        String locationHeader = 지하철_노선_생성_요청_LOCATION_헤더_추출(createdResponse);
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> green = 지하철_노선_수정_요청(locationHeader, "2호선", "green",
            station1.getId(), station2.getId(), 10);
        LineResponse updatedLineResponse = 지하철_노선_응답_객체_추출(green);
        // then
        // 지하철_노선_수정됨
        Assertions.assertAll(() -> {
            assertThat(updatedLineResponse.getId()).isEqualTo(createdLineResponse.getId());
            assertThat(updatedLineResponse.getName()).isEqualTo("2호선");
            assertThat(updatedLineResponse.getColor()).isEqualTo("green");
        });
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(line1);
        LineResponse createdLineResponse = 지하철_노선_응답_객체_추출(createdResponse);
        String locationHeader = 지하철_노선_생성_요청_LOCATION_헤더_추출(createdResponse);
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> result = 지하철_노선_제거_요청(locationHeader);
        // then
        // 지하철_노선_삭제됨
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(locationHeader);
        Assertions.assertAll(() -> {
            assertThat(result.statusCode()).isEqualTo(HttpStatus.OK.value());
            assertThat(response.statusCode()).isNotEqualTo(HttpStatus.OK.value());
        });

    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(LineResponse response) {
        return RestAssured.given()
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .when()
            .get("/lines/{lineId}", response.getId())
            .then()
            .log().all()
            .extract();
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(String locationHeader) {
        return RestAssured.given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            get(locationHeader).
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_생성_요청(LineRequest lineRequest) {
        return RestAssured.given().
            body(lineRequest).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            post("/lines").
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String location, String name,
        String color, Long upStationId, Long downStationId, int distance) {
        Map<String, Object> params = 지하철_노선_생성_요청_파라미터(name, color, upStationId, downStationId,
            distance);
        return 지하철_노선_수정_요청(location, params);
    }

    private static Map<String, Object> 지하철_노선_생성_요청_파라미터(String name, String color, Long upStationId,
        Long downStationId, int distance) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(String location,
        Map<String, Object> params) {
        return RestAssured.given().
            body(params).
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            put(location).
            then().
            log().all().
            extract();
    }

    public static ExtractableResponse<Response> 지하철_노선_제거_요청(String location) {
        return RestAssured.given().
            contentType(MediaType.APPLICATION_JSON_VALUE).
            when().
            delete(location).
            then().
            log().all().
            extract();
    }

    private ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        return 지하철_노선_조회_요청("/lines");
    }

    private List<Long> 지하철_노선_응답_객체_아이디_추출(List<LineResponse> lineResponses) {
        return lineResponses.stream().map(LineResponse::getId).collect(Collectors.toList());
    }

    private LineResponse 지하철_노선_응답_객체_추출(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class);
    }

    private String 지하철_노선_생성_요청_LOCATION_헤더_추출(ExtractableResponse<Response> response) {
        return response.header("Location");
    }


    private LineRequest 지하철_노선_생성_파라미터(String name, String color, Long upStationId,
        Long downStationId,
        int distance) {
        return new LineRequest(name, color, upStationId, downStationId, distance);
    }


}
