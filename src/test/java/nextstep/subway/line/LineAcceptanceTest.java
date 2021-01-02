package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineCreateResponse;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성됨
        노선_생성_응답_검증(response, "bg-red-600", "신분당선");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        지하철_노선_생성_요청(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 종점역(상행,하행)을 함께 추가해 생성한다.")
    @Test
    void createLineWithUpDownStation() {
        // when
        // 종점역 생성 요청
        Map<String, String> stationParams1 = new HashMap<>();
        stationParams1.put("name", "강남역");
        ExtractableResponse<Response> response1 = StationAcceptanceTest.지하철_생성_요청(stationParams1);
        long stationId1 = StationAcceptanceTest.지하철_생성_응답에서_id_추출(response1);

        Map<String, String> stationParams2 = new HashMap<>();
        stationParams2.put("name", "역삼역");
        ExtractableResponse<Response> response2 = StationAcceptanceTest.지하철_생성_요청(stationParams2);
        long stationId2 = StationAcceptanceTest.지하철_생성_응답에서_id_추출(response2);

        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", String.valueOf(stationId1));
        params.put("downStationId", String.valueOf(stationId2));
        params.put("distance", String.valueOf(10));
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성됨
        노선_생성_응답_검증(response, "bg-red-600", "신분당선", stationId1, stationId2, 10);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 종점역 생성 요청
        Map<String, String> stationParams1 = new HashMap<>();
        stationParams1.put("name", "강남역");
        ExtractableResponse<Response> response1 = StationAcceptanceTest.지하철_생성_요청(stationParams1);
        long stationId1 = StationAcceptanceTest.지하철_생성_응답에서_id_추출(response1);

        Map<String, String> stationParams2 = new HashMap<>();
        stationParams2.put("name", "역삼역");
        ExtractableResponse<Response> response2 = StationAcceptanceTest.지하철_생성_요청(stationParams2);
        long stationId2 = StationAcceptanceTest.지하철_생성_응답에서_id_추출(response2);

        Map<String, String> stationParams3 = new HashMap<>();
        stationParams3.put("name", "선릉역");
        ExtractableResponse<Response> response3 = StationAcceptanceTest.지하철_생성_요청(stationParams3);
        long stationId3 = StationAcceptanceTest.지하철_생성_응답에서_id_추출(response3);

        // 지하철_노선_등록되어_있음
        Map<String, String> params1 = new HashMap<>();
        params1.put("color", "bg-red-600");
        params1.put("name", "신분당선");
        params1.put("upStationId", String.valueOf(stationId1));
        params1.put("downStationId", String.valueOf(stationId2));
        params1.put("distance", String.valueOf(7));
        ExtractableResponse<Response> createResponse1 = 지하철_노선_생성_요청(params1);

        // 지하철_노선_등록되어_있음
        Map<String, String> params2 = new HashMap<>();
        params2.put("color", "bg-green-600");
        params2.put("name", "2호선");
        params2.put("upStationId", String.valueOf(stationId2));
        params2.put("downStationId", String.valueOf(stationId3));
        params2.put("distance", String.valueOf(12));
        ExtractableResponse<Response> createResponse2 = 지하철_노선_생성_요청(params2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        // 지하철_노선_목록_포함됨
        List<Long> expectedLineIds = Arrays.asList(createResponse1, createResponse2).stream()
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());
        List<Long> resultLineIds = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());
        assertThat(resultLineIds).containsAll(expectedLineIds);

        LineResponse lineResponse1 = response.jsonPath().getObject("[0]", LineResponse.class);
        노선_응답_종점_검증(lineResponse1, stationId1, stationId2, 7);
        LineResponse lineResponse2 = response.jsonPath().getObject("[1]", LineResponse.class);
        노선_응답_종점_검증(lineResponse2, stationId2, stationId3, 12);
    }

    @DisplayName("지하철 노선 목록을 조회시 아무 것도 없을때")
    @Test
    void getLines2() {
        //given
        //when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
                .then().log().all().extract();
        //then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.jsonPath().getList(".", LineResponse.class).size()).isEqualTo(0);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 종점역 생성 요청
        Map<String, String> stationParams1 = new HashMap<>();
        stationParams1.put("name", "강남역");
        ExtractableResponse<Response> response1 = StationAcceptanceTest.지하철_생성_요청(stationParams1);
        long stationId1 = StationAcceptanceTest.지하철_생성_응답에서_id_추출(response1);

        Map<String, String> stationParams2 = new HashMap<>();
        stationParams2.put("name", "역삼역");
        ExtractableResponse<Response> response2 = StationAcceptanceTest.지하철_생성_요청(stationParams2);
        long stationId2 = StationAcceptanceTest.지하철_생성_응답에서_id_추출(response2);

        // 지하철_노선_등록되어_있음
        int distance = 10;
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        params.put("upStationId", String.valueOf(stationId1));
        params.put("downStationId", String.valueOf(stationId2));
        params.put("distance", String.valueOf(distance));
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(params);
        long createdId = 노선_응답에서_id_추출(createResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_단건조회_요청(createdId);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        노선_응답_검증(response, "bg-red-600", "신분당선");

        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        노선_응답_종점_검증(lineResponse, stationId1, stationId2, distance);
    }

    @DisplayName("존재하지 않은 지하철 1개 노선을 조회한다.")
    @Test
    void getLine2() {
        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_단건조회_요청(1L);

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(params);
        long createdId = 노선_응답에서_id_추출(createResponse);

        // when
        // 지하철_노선_수정_요청
        Map<String, String> putParams = new HashMap<>();
        putParams.put("color", "bg-blue-600");
        putParams.put("name", "구분당선");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdId, putParams);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정")
    @Test
    void updateLine2() {
        // when
        // 지하철_노선_수정_요청
        Map<String, String> putParams = new HashMap<>();
        putParams.put("color", "bg-blue-600");
        putParams.put("name", "구분당선");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(1, putParams);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> params = new HashMap<>();
        params.put("color", "bg-red-600");
        params.put("name", "신분당선");
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(params);
        long createdId = 노선_응답에서_id_추출(createResponse);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdId);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 제거한다.")
    @Test
    void deleteLine2() {
        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(1);

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Map<String, String> params) {
        return RestAssured
                .given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/lines")
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_단건조회_요청(long lineId) {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines/" + lineId)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(long lineId, Map<String, String> putParams) {
        return RestAssured
                .given().log().all()
                .body(putParams)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().put("/lines/" + lineId)
                .then().log().all().extract();
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(long lineId) {
        return RestAssured
                .given().log().all()
                .when().delete("/lines/" + lineId)
                .then().log().all().extract();
    }

    private void 노선_응답_검증(ExtractableResponse<Response> response, String color, String name) {
        LineResponse lineResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getColor()).isEqualTo(color);
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getCreatedDate()).isNotNull();
        assertThat(lineResponse.getModifiedDate()).isNotNull();
    }

    private void 노선_응답_종점_검증(LineResponse response, Long expectedUpStationId, Long expectedDownStationId,
                             Integer expectedDistance) {
        int stationsSize = response.getStations().size();
        assertThat(stationsSize).isEqualTo(2);

        StationResponse station1 = response.getStations().get(0);
        assertThat(station1.getId()).isEqualTo(expectedUpStationId);

        StationResponse station2 = response.getStations().get(1);
        assertThat(station2.getId()).isEqualTo(expectedDownStationId);

        assertThat(response.getDistance()).isEqualTo(expectedDistance);
    }

    private void 노선_생성_응답_검증(ExtractableResponse<Response> response, String color, String name) {
        노선_생성_응답_검증(response, color, name, null, null, 0);
    }

    private void 노선_생성_응답_검증(ExtractableResponse<Response> response, String color, String name,
                             Long upStationId, Long downStationId, Integer distance) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        assertThat(response.header("Location")).startsWith("/lines/");

        LineCreateResponse lineResponse = response.jsonPath().getObject(".", LineCreateResponse.class);
        assertThat(lineResponse.getId()).isNotNull();
        assertThat(lineResponse.getColor()).isEqualTo(color);
        assertThat(lineResponse.getName()).isEqualTo(name);
        assertThat(lineResponse.getCreatedDate()).isNotNull();
        assertThat(lineResponse.getModifiedDate()).isNotNull();
        assertThat(lineResponse.getUpStationId()).isEqualTo(upStationId);
        assertThat(lineResponse.getDownStationId()).isEqualTo(downStationId);
        assertThat(lineResponse.getDistance()).isEqualTo(distance);
    }

    private long 노선_응답에서_id_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }
}
