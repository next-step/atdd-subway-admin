package nextstep.subway.section.application;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 추가 관련 인수테스트")
public class SectionAcceptanceTest extends AcceptanceTest {

    private long existedLineUpStationId;
    private long existedLineDownStationId;
    private long tryRegisterStationId;
    private ExtractableResponse<Response> lineId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        existedLineUpStationId = 지하철_역_생성_후_ID_반환("광교");
        existedLineDownStationId = 지하철_역_생성_후_ID_반환("강남");
        tryRegisterStationId = 지하철_역_생성_후_ID_반환("양재");

        lineId = 지하철_노선_등록("신분당선", "g-123", existedLineUpStationId, existedLineDownStationId, 100);
    }

    @Test
    void 역_사이에_새로운_역을_등록할_경우() {

        // when
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", existedLineUpStationId);
        params.put("downStationId", tryRegisterStationId);
        params.put("distance", 50);

        ExtractableResponse<Response> response = given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("lines/" + lineId + "/sections")
                .then().log().all().extract();

        // then
        List<Long> stations = response.body().jsonPath().getObject(".", LineResponse.class).getStations().stream()
                .map(Station::getId).collect(Collectors.toList());

        assertThat(stations).containsExactly(existedLineUpStationId, tryRegisterStationId, existedLineDownStationId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }


    @Test
    void 새로운_역을_상행_종점으로_등록할_경우_인수_테스트_작성() {
        // when
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", tryRegisterStationId);
        params.put("downStationId", existedLineUpStationId);
        params.put("distance", 50);

        ExtractableResponse<Response> response = given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("lines/" + lineId + "/sections")
                .then().log().all().extract();

        List<Long> stations = response.body().jsonPath().getObject(".", LineResponse.class).getStations().stream()
                .map(Station::getId).collect(Collectors.toList());

        assertThat(stations).containsExactly(tryRegisterStationId, existedLineUpStationId, existedLineDownStationId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 새로운_역을_하행_종점으로_등록할_경우_인수() {
        // when
        Map<String, Object> params = new HashMap<>();
        params.put("upStationId", existedLineDownStationId);
        params.put("downStationId", tryRegisterStationId);
        params.put("distance", 50);

        ExtractableResponse<Response> response = given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("lines/" + lineId + "/sections")
                .then().log().all().extract();

        List<Long> stations = response.body().jsonPath().getObject(".", LineResponse.class).getStations().stream()
                .map(Station::getId).collect(Collectors.toList());

        assertThat(stations).containsExactly(existedLineUpStationId, existedLineDownStationId, tryRegisterStationId);
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    private ExtractableResponse<Response> 지하철_노선_등록(String 이름, String 색깔, Long 상행종착역ID, Long 하행종착역ID, int 거리) {
        Map<String, Object> originLine = new HashMap<>();
        originLine.put("name", 이름);
        originLine.put("color", 색깔);
        originLine.put("upStationId", 상행종착역ID);
        originLine.put("downStationId", 하행종착역ID);
        originLine.put("distance", 거리);

        return given().log().all()
                .when()
                .body(originLine)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .post("/lines")
                .then()
                .log().all()
                .extract();
    }

    private static long 지하철_역_생성_후_ID_반환(String 이름) {
        Map<String, String> params = new HashMap<>();
        params.put("name", 이름);

        // when
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .body(params)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/stations")
                .then().log().all()
                .extract();

        return Long.parseLong(response.header("Location").split("/")[2]);
    }
}
