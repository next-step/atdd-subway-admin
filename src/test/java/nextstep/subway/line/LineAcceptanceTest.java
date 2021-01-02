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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        Map<String, String> 신분당선Params = new HashMap<>();
        신분당선Params.put("color", "bg-red-600");
        신분당선Params.put("name", "신분당선");
        ExtractableResponse<Response> 신분당선Response = 지하철_노선_생성_요청(신분당선Params);

        // then
        // 지하철_노선_생성됨
        노선_생성_응답_검증(신분당선Response, "bg-red-600", "신분당선");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선Params = new HashMap<>();
        신분당선Params.put("color", "bg-red-600");
        신분당선Params.put("name", "신분당선");
        지하철_노선_생성_요청(신분당선Params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> 신분당선Response = 지하철_노선_생성_요청(신분당선Params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(신분당선Response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선을 종점역(상행,하행)을 함께 추가해 생성한다.")
    @Test
    void createLineWithUpDownStation() {
        // when
        // 종점역 생성 요청
        Map<String, String> 강남역Params = new HashMap<>();
        강남역Params.put("name", "강남역");
        ExtractableResponse<Response> 강남역Response = StationAcceptanceTest.지하철_생성_요청(강남역Params);
        long 강남역Id = StationAcceptanceTest.지하철_생성_응답에서_id_추출(강남역Response);

        Map<String, String> 역삼역Params = new HashMap<>();
        역삼역Params.put("name", "역삼역");
        ExtractableResponse<Response> 역삼역Response = StationAcceptanceTest.지하철_생성_요청(역삼역Params);
        long 역삼역Id = StationAcceptanceTest.지하철_생성_응답에서_id_추출(역삼역Response);

        // 지하철_노선_생성_요청
        Map<String, String> 신분당선Params = new HashMap<>();
        신분당선Params.put("color", "bg-red-600");
        신분당선Params.put("name", "신분당선");
        신분당선Params.put("upStationId", String.valueOf(강남역Id));
        신분당선Params.put("downStationId", String.valueOf(역삼역Id));
        신분당선Params.put("distance", String.valueOf(10));
        ExtractableResponse<Response> 신분당선Response = 지하철_노선_생성_요청(신분당선Params);

        // then
        // 지하철_노선_생성됨
        노선_생성_응답_검증(신분당선Response, "bg-red-600", "신분당선", 강남역Id, 역삼역Id, 10);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 종점역 생성 요청
        Map<String, String> 강남역Params = new HashMap<>();
        강남역Params.put("name", "강남역");
        ExtractableResponse<Response> 강남역Response = StationAcceptanceTest.지하철_생성_요청(강남역Params);
        long 강남역Id = StationAcceptanceTest.지하철_생성_응답에서_id_추출(강남역Response);

        Map<String, String> 역삼역Params = new HashMap<>();
        역삼역Params.put("name", "역삼역");
        ExtractableResponse<Response> 역삼역Response = StationAcceptanceTest.지하철_생성_요청(역삼역Params);
        long 역삼역Id = StationAcceptanceTest.지하철_생성_응답에서_id_추출(역삼역Response);

        Map<String, String> 선릉역Params = new HashMap<>();
        선릉역Params.put("name", "선릉역");
        ExtractableResponse<Response> 선릉역Response = StationAcceptanceTest.지하철_생성_요청(선릉역Params);
        long 선릉역Id = StationAcceptanceTest.지하철_생성_응답에서_id_추출(선릉역Response);

        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선Params = new HashMap<>();
        신분당선Params.put("color", "bg-red-600");
        신분당선Params.put("name", "신분당선");
        신분당선Params.put("upStationId", String.valueOf(강남역Id));
        신분당선Params.put("downStationId", String.valueOf(역삼역Id));
        신분당선Params.put("distance", String.valueOf(7));
        ExtractableResponse<Response> 신분당선Response = 지하철_노선_생성_요청(신분당선Params);

        // 지하철_노선_등록되어_있음
        Map<String, String> 제2호선Params = new HashMap<>();
        제2호선Params.put("color", "bg-green-600");
        제2호선Params.put("name", "2호선");
        제2호선Params.put("upStationId", String.valueOf(역삼역Id));
        제2호선Params.put("downStationId", String.valueOf(선릉역Id));
        제2호선Params.put("distance", String.valueOf(12));
        ExtractableResponse<Response> 제2호선Response = 지하철_노선_생성_요청(제2호선Params);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> 노선목록조회Response = 지하철_노선_목록조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(노선목록조회Response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(노선목록조회Response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        노선_목록_ID_검증(신분당선Response, 제2호선Response, 노선목록조회Response);

        노선_응답_종점_검증(노선목록조회Response.jsonPath().getObject("[0]", LineResponse.class),
                강남역Id, 역삼역Id, 7);
        노선_응답_종점_검증(노선목록조회Response.jsonPath().getObject("[1]", LineResponse.class),
                역삼역Id, 선릉역Id, 12);
    }

    @DisplayName("지하철 노선 목록을 조회시 아무 것도 없을때")
    @Test
    void getLines2() {
        //given
        //when
        ExtractableResponse<Response> response = 지하철_노선_목록조회_요청();
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
        Map<String, String> 강남역Params = new HashMap<>();
        강남역Params.put("name", "강남역");
        ExtractableResponse<Response> 강남역Response = StationAcceptanceTest.지하철_생성_요청(강남역Params);
        long 강남역Id = StationAcceptanceTest.지하철_생성_응답에서_id_추출(강남역Response);

        Map<String, String> 역삼역Params = new HashMap<>();
        역삼역Params.put("name", "역삼역");
        ExtractableResponse<Response> 역삼역Response = StationAcceptanceTest.지하철_생성_요청(역삼역Params);
        long 역삼역Id = StationAcceptanceTest.지하철_생성_응답에서_id_추출(역삼역Response);

        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선Params = new HashMap<>();
        신분당선Params.put("color", "bg-red-600");
        신분당선Params.put("name", "신분당선");
        신분당선Params.put("upStationId", String.valueOf(강남역Id));
        신분당선Params.put("downStationId", String.valueOf(역삼역Id));
        신분당선Params.put("distance", String.valueOf(10));
        ExtractableResponse<Response> createResponse = 지하철_노선_생성_요청(신분당선Params);
        long 신분당선Id = 노선_응답에서_ID_추출(createResponse);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> 단건조회Response = 지하철_노선_단건조회_요청(신분당선Id);

        // then
        // 지하철_노선_응답됨
        assertThat(단건조회Response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(단건조회Response.contentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);

        노선_응답_검증(단건조회Response, "bg-red-600", "신분당선");

        LineResponse 신분당선LineResponse = 단건조회Response.jsonPath().getObject(".", LineResponse.class);
        노선_응답_종점_검증(신분당선LineResponse, 강남역Id, 역삼역Id, 10);
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
        Map<String, String> 신분당선Params = new HashMap<>();
        신분당선Params.put("color", "bg-red-600");
        신분당선Params.put("name", "신분당선");
        ExtractableResponse<Response> 노선생성Response = 지하철_노선_생성_요청(신분당선Params);
        long createdId = 노선_응답에서_ID_추출(노선생성Response);

        // when
        // 지하철_노선_수정_요청
        Map<String, String> 구분당선Params = new HashMap<>();
        구분당선Params.put("color", "bg-blue-600");
        구분당선Params.put("name", "구분당선");
        ExtractableResponse<Response> 노선수정Response = 지하철_노선_수정_요청(createdId, 구분당선Params);

        // then
        // 지하철_노선_수정됨
        assertThat(노선수정Response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("존재하지 않는 지하철 노선을 수정")
    @Test
    void updateLine2() {
        // when
        // 지하철_노선_수정_요청
        Map<String, String> 구분당선Params = new HashMap<>();
        구분당선Params.put("color", "bg-blue-600");
        구분당선Params.put("name", "구분당선");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(1, 구분당선Params);

        // then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, String> 신분당선Params = new HashMap<>();
        신분당선Params.put("color", "bg-red-600");
        신분당선Params.put("name", "신분당선");
        ExtractableResponse<Response> 신분당선Response = 지하철_노선_생성_요청(신분당선Params);
        long 신분당선Id = 노선_응답에서_ID_추출(신분당선Response);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선Id);

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

    private ExtractableResponse<Response> 지하철_노선_목록조회_요청() {
        return RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/lines")
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

    private long 노선_응답에서_ID_추출(ExtractableResponse<Response> response) {
        return response.jsonPath().getLong("id");
    }

    private void 노선_목록_ID_검증(ExtractableResponse<Response> createResponse1,
                             ExtractableResponse<Response> createResponse2,
                             ExtractableResponse<Response> findAllResponse) {

        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = findAllResponse.jsonPath().getList(".", LineResponse.class)
                .stream()
                .map(LineResponse::getId)
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }
}
