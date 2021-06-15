package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private static final String FIRST_LINE_NAME = "1호선";
    private static final String SECOND_LINE_NAME = "2호선";
    private static final String BLACK_LINE_COLOR = "black";
    private static final String RED_LINE_COLOR = "red";
    private static final String LINE_REQUEST_PATH = "/lines";

    private ExtractableResponse<Response> savedLineResponse1;
    private ExtractableResponse<Response> savedLineResponse2;
    private long savedUpStationId;
    private long savedDownStationId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        //given 상행선&&하행선을 포함한 노선1,2 생성
        ExtractableResponse<Response> responseUpStation = StationAcceptanceTest.requestCreateStation("상행선");
        ExtractableResponse<Response> responseDownStation = StationAcceptanceTest.requestCreateStation("하행선");

        savedUpStationId = responseUpStation.jsonPath().getObject(".", StationResponse.class).getId();
        savedDownStationId = responseDownStation.jsonPath().getObject(".", StationResponse.class).getId();

        savedLineResponse1 = requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR, savedUpStationId, savedDownStationId, 10);
        savedLineResponse2 = requestCreateLine(SECOND_LINE_NAME, RED_LINE_COLOR, savedUpStationId, savedDownStationId, 5);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // then 지하철_노선_생성됨
        assertHttpStatus(savedLineResponse1, HttpStatus.CREATED);
        assertThat(savedLineResponse1.header("Location")).isNotBlank();
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplication() {
        // when 지하철_동일이름_노선_생성_요청
        ExtractableResponse<Response> response = requestCreateLine(FIRST_LINE_NAME, BLACK_LINE_COLOR, savedUpStationId, savedDownStationId, 10);
        // then 지하철_노선_생성_실패됨
        assertHttpStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // when 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get(LINE_REQUEST_PATH + "/1")
                .then().log().all().extract();
        // then 지하철_노선_역이포함되어_응답됨
        assertHttpStatus(response, HttpStatus.OK);
        assertThat(response.jsonPath().getObject(".", LineResponse.class)).isNotNull();
        assertThat(response.jsonPath().getList("stations").size()).isEqualTo(2);

    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // when 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .when().get(LINE_REQUEST_PATH)
                .then().log().all().extract();
        // then 지하철_노선_목록_응답됨
        assertHttpStatus(response, HttpStatus.OK);
        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Stream.of(savedLineResponse1, savedLineResponse2)
                .map(expectedResponse -> expectedResponse.jsonPath().getObject(".", LineResponse.class).getId())
                .collect(Collectors.toList());
        List<Long> actualLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());
        assertThat(actualLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // when 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(makeParams(FIRST_LINE_NAME, RED_LINE_COLOR), 1L);
        // then 지하철_노선_수정됨
        String actualColor = response.jsonPath().get("color").toString();
        assertThat(actualColor).isEqualTo(RED_LINE_COLOR);
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정한다.")
    @Test
    void updateNonExistentLine() {
        // when 지하철_노선_수정_요청
        ExtractableResponse<Response> response = requestUpdateLine(makeParams(FIRST_LINE_NAME, RED_LINE_COLOR), 3L);
        // then 지하철_노선_수정_실패됨
        assertHttpStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // when 지하철_노선_제거_요청
        ExtractableResponse<Response> response = requestDeleteLine(1L);
        // then 지하철_노선_삭제됨
        assertHttpStatus(response, HttpStatus.NO_CONTENT);
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteNonExistentLine() {
        // when 지하철_노선_제거_요청
        ExtractableResponse<Response> response = requestDeleteLine(3L);
        // then 지하철_노선_삭제됨
        assertHttpStatus(response, HttpStatus.BAD_REQUEST);
    }

    private void assertHttpStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }

    private ExtractableResponse<Response> requestCreateLine(String name, String color) {
        Map<String, String> params = makeParams(name, color);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_REQUEST_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestCreateLine(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = makeParams(name, color, upStationId, downStationId, distance);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post(LINE_REQUEST_PATH)
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> requestUpdateLine(Map<String, String> params, Long id) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put(LINE_REQUEST_PATH + "/" + id)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> requestDeleteLine(Long id) {
        return RestAssured
                .given().log().all()
                .when().delete(LINE_REQUEST_PATH + "/" + id)
                .then().log().all().extract();
    }

    private Map<String, String> makeParams(String name, String color) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        return params;
    }

    private Map<String, String> makeParams(String name, String color, Long upStationId, Long downStationId, int distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance + "");
        return params;
    }
}