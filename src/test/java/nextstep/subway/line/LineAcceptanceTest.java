package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능 리펙토링")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("지하철 노선 생성")
    void createLine() {
        //given : 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        String upStationName = "강남역";
        String downStationName = "광교역";
        ExtractableResponse<Response> upStationResponse = createTestStationData(upStationName);
        ExtractableResponse<Response> downStationResponse = createTestStationData(downStationName);
        String upStationId = upStationResponse.header("Location").split("/")[2];
        String downStationId = downStationResponse.header("Location").split("/")[2];

        // given: 구간 거리 값을 알고 있다.
        String distance = "100";

        // when: 신분당선 지하철 노선 생성 요청 한다.
        Map<String, String> defaultParams = createDefaultParams("신분당선", upStationId, downStationId, distance);
        ExtractableResponse<Response> response = createTestLineData(defaultParams);

        // then: 지하철 노선이 생성된다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header("Location")).isNotBlank();

    }

    @Test
    @DisplayName("이미 등록된 지하철 노선 이름으로 지하철 노선을 생성")
    void createLine2() {
        //given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        String upStationName = "강남역";
        String downStationName = "광교역";
        ExtractableResponse<Response> upStationResponse = createTestStationData(upStationName);
        ExtractableResponse<Response> downStationResponse = createTestStationData(downStationName);
        String upStationId = upStationResponse.header("Location").split("/")[2];
        String downStationId = downStationResponse.header("Location").split("/")[2];

        //given: 구간 거리 값을 알고 있다.
        String distance = "100";

        //given: 신분당선 지하철 노선이 등록되어 있다.
        Map<String, String> params = createDefaultParams("신분당선", upStationId, downStationId, distance);
        createTestLineData(params);

        // when: 신분당선 지하철 노선 생성 요청 한다.
        ExtractableResponse<Response> response = createTestLineData(params);

        // then: 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());

    }

    @Test
    @DisplayName("지하철 노선 목록 조회")
    void getLines() {
        //given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        String upStationName = "강남역";
        String downStationName = "광교역";
        String lineName = "신분당선";
        ExtractableResponse<Response> upStationResponse = createTestStationData(upStationName);
        ExtractableResponse<Response> downStationResponse = createTestStationData(downStationName);
        String upStationId = upStationResponse.header("Location").split("/")[2];
        String downStationId = downStationResponse.header("Location").split("/")[2];

        //given: 구간 거리 값을 알고 있다.
        String distance = "100";

        //given: 신분당선 지하철 노선이 등록되어 있다.
        Map<String, String> params = createDefaultParams(lineName, upStationId, downStationId, distance);
        createTestLineData(params);

        //given: 2호선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        upStationName = "사당역";
        downStationName = "잠실역";
        lineName = "2호선";
        upStationResponse = createTestStationData(upStationName);
        downStationResponse = createTestStationData(downStationName);
        upStationId = upStationResponse.header("Location").split("/")[2];
        downStationId = downStationResponse.header("Location").split("/")[2];

        //given: 구간 거리 값을 알고 있다.
        distance = "50";
        //given: 2호선 지하철 노선이 등록되어 있다.
        params = createDefaultParams(lineName, upStationId, downStationId, distance);
        createTestLineData(params);

        //when: 지하철 노선 목록 조회를 요청 한다
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines")
                .then().log().all()
                .extract();

        // then: 지하철 노선 목록이 응답된다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        // then: 지하철 노선 목록에 신분당선, 2호선이 포함되어 있다.
        List<LineResponse> lines = response
                .body()
                .jsonPath()
                .getList("$", LineResponse.class)
                .stream()
                .collect(Collectors.toList());

        // then: 지하철 노선 목록에 신분당선, 2호선이 포함되어 있다.
        assertThat(lines.stream().map(LineResponse::getName).collect(Collectors.toList())).contains("신분당선", "2호선");
        // then: 지하철 노선 목록에 신분당선, 2호선의 상행, 하행 종점역 정보가 존재한다.
        String expected = response.body().jsonPath().getString("$");
        assertThat(expected).contains("강남역", "광교역", "사당역", "잠실역");

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        //given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        String upStationName = "강남역";
        String downStationName = "광교역";
        ExtractableResponse<Response> upStationResponse = createTestStationData(upStationName);
        ExtractableResponse<Response> downStationResponse = createTestStationData(downStationName);
        String upStationId = upStationResponse.header("Location").split("/")[2];
        String downStationId = downStationResponse.header("Location").split("/")[2];

        //given: 구간 거리 값을 알고 있다.
        String distance = "100";

        // given: 지하철 노선 신분당선이 등록되어 있다.
        Map<String, String> params = createDefaultParams("신분당선", upStationId, downStationId, distance);
        ExtractableResponse<Response> createResponse = createTestLineData(params);
        String createdId = createResponse.header("Location").split("/")[2];

        // when: 지하철 노선 신분당선 조회를 요청한다.
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/" + createdId)
                .then().log().all()
                .extract();

        // then: 지하철 노선이 응답된다.
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());

        String expected = response.body().jsonPath().getString("$");
        assertThat(expected).contains("신분당선");

        // then: 지하철 노선 목록에 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 존재한다.
        assertThat(expected).contains("강남역", "강남역");
    }

    @DisplayName("노선 id가 없는 지하철 노선을 조회한다.")
    @Test
    void getLine2() {
        //given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        String upStationName = "강남역";
        String downStationName = "광교역";
        ExtractableResponse<Response> upStationResponse = createTestStationData(upStationName);
        ExtractableResponse<Response> downStationResponse = createTestStationData(downStationName);
        String upStationId = upStationResponse.header("Location").split("/")[2];
        String downStationId = downStationResponse.header("Location").split("/")[2];

        //given: 구간 거리 값을 알고 있다.
        String distance = "100";

        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams("신분당선", upStationId, downStationId, distance);
        createTestLineData(params);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .get("/lines/100")
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        //given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        String upStationName = "강남역";
        String downStationName = "광교역";
        ExtractableResponse<Response> upStationResponse = createTestStationData(upStationName);
        ExtractableResponse<Response> downStationResponse = createTestStationData(downStationName);
        String upStationId = upStationResponse.header("Location").split("/")[2];
        String downStationId = downStationResponse.header("Location").split("/")[2];

        //given: 구간 거리 값을 알고 있다.
        String distance = "100";

        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams("신분당선", upStationId, downStationId, distance);
        ExtractableResponse<Response> createResponse = createTestLineData(params);
        String createdId = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_수정_요청
        Map<String, String> putParams = new HashMap<>();
        putParams.put("color", "bg-blue-600");
        putParams.put("name", "구분당선");
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(putParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put("/lines/" + createdId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        //given: 신분당선의 상행 종점 지하철역, 하행 종점 지하철역이 생성되어 있다.
        String upStationName = "강남역";
        String downStationName = "광교역";
        ExtractableResponse<Response> upStationResponse = createTestStationData(upStationName);
        ExtractableResponse<Response> downStationResponse = createTestStationData(downStationName);
        String upStationId = upStationResponse.header("Location").split("/")[2];
        String downStationId = downStationResponse.header("Location").split("/")[2];

        //given: 구간 거리 값을 알고 있다.
        String distance = "100";

        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = createDefaultParams("신분당선", upStationId, downStationId, distance);
        ExtractableResponse<Response> createResponse = createTestLineData(params);
        String createdId = createResponse.header("Location").split("/")[2];

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when()
                .delete("/lines/" + createdId)
                .then().log().all()
                .extract();

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private Map<String, String> createDefaultParams(String lineName, String upStationId, String downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", lineName);
        params.put("upStationId", upStationId);
        params.put("downStationId", downStationId);
        params.put("distance", distance);
        return params;
    }

    private ExtractableResponse<Response> createTestLineData(Map<String, String> params) {
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/lines")
                .then().log().all()
                .extract();
    }

    private ExtractableResponse<Response> createTestStationData(String stationName) {
        Map<String, String> params = new HashMap<>();
        params.put("name", stationName);
        return RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();
    }
}
