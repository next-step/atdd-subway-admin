package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.RestAcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends RestAcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // 상행역_하행역_등록되어_있음
        Map<String, String> stationRegisterParam1 = new HashMap<>();
        stationRegisterParam1.put("name", "강남");
        executePost("/stations", stationRegisterParam1);

        Map<String, String> stationRegisterParam2 = new HashMap<>();
        stationRegisterParam2.put("name", "광교");
        executePost("/stations", stationRegisterParam2);
        
        // when
        // 지하철_노선_생성_요청
        Map<String, String> params = new HashMap<>();
        params.put("name", "신분당선");
        params.put("color", "red");
        params.put("upStationId", "1");
        params.put("downStationId", "2");
        params.put("distance", "40");

        ExtractableResponse<Response> response = executePost("/lines", params);

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        saveShinBundangLine();

        // when
        // 지하철_노선_생성_요청
        Map<String, String> stationRegisterParam1 = new HashMap<>();
        stationRegisterParam1.put("name", "상현");
        executePost("/stations", stationRegisterParam1);

        Map<String, String> stationRegisterParam2 = new HashMap<>();
        stationRegisterParam2.put("name", "양재");
        executePost("/stations", stationRegisterParam2);

        Map<String, String> registerParams = new HashMap<>();
        registerParams.put("name", "신분당선");
        registerParams.put("color", "red");
        registerParams.put("upStationId", "3");
        registerParams.put("downStationId", "4");
        registerParams.put("distance", "40");

        ExtractableResponse<Response> response = executePost("/lines", registerParams);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        saveShinBundangLine();

        // 지하철_노선_등록되어_있음
        saveLine2();

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = executeGet("/lines");

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<String> lineNames = response.jsonPath().getList(".", LineResponse.class).stream()
                .map(LineResponse::getName)
                .collect(Collectors.toList());
        assertThat(lineNames).isEqualTo(Arrays.asList("신분당선", "2호선"));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> registerResponse = saveShinBundangLine();

        // when
        // 지하철_노선_조회_요청
        String savedUri = registerResponse.header("Location");
        ExtractableResponse<Response> getResponse = executeGet(savedUri);

        // then
        // 지하철_노선_응답됨
        assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = getResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("신분당선");
        assertThat(lineResponse.getColor()).isEqualTo("red");
    }

    @DisplayName("지하철 노선을 종점역과 함께 조회한다.")
    @Test
    void getLineWithLastStop() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> registerResponse = saveShinBundangLine();

        // when
        // 지하철_노선_조회_요청
        String savedUri = registerResponse.header("Location");
        ExtractableResponse<Response> getResponse = executeGet(savedUri);

        // then
        // 지하철_노선_응답됨
        LineResponse lineResponse = getResponse.jsonPath().getObject(".", LineResponse.class);
        StationResponse upStation = lineResponse.getStations().get(0);
        StationResponse downStation = lineResponse.getStations().get(lineResponse.getStations().size()-1);
        assertAll(
                () -> assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(lineResponse.getStations().size()).isEqualTo(2),
                () -> assertThat(upStation.getName()).isEqualTo("강남"),
                () -> assertThat(downStation.getName()).isEqualTo("광교")
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> registerResponse = saveShinBundangLine();

        // when
        // 지하철_노선_수정_요청
        String savedUri = registerResponse.header("Location");
        Map<String, String> editParams = new HashMap<>();
        editParams.put("name", "2호선");
        editParams.put("color", "green");
        ExtractableResponse<Response> editResponse = executePut(savedUri, editParams);

        // then
        // 지하철_노선_수정됨
        assertThat(editResponse.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse lineResponse = editResponse.jsonPath().getObject(".", LineResponse.class);
        assertThat(lineResponse.getName()).isEqualTo("2호선");
        assertThat(lineResponse.getColor()).isEqualTo("green");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> registerResponse = saveShinBundangLine();

        // when
        // 지하철_노선_제거_요청
        String savedUri = registerResponse.header("Location");
        ExtractableResponse<Response> deleteResponse = executeDelete(savedUri);

        // then
        // 지하철_노선_삭제됨
        assertThat(deleteResponse.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    private ExtractableResponse<Response> saveShinBundangLine() {
        Map<String, String> stationRegisterParam1 = new HashMap<>();
        stationRegisterParam1.put("name", "강남");
        executePost("/stations", stationRegisterParam1);

        Map<String, String> stationRegisterParam2 = new HashMap<>();
        stationRegisterParam2.put("name", "광교");
        executePost("/stations", stationRegisterParam2);

        Map<String, String> registerParams = new HashMap<>();
        registerParams.put("name", "신분당선");
        registerParams.put("color", "red");
        registerParams.put("upStationId", "1");
        registerParams.put("downStationId", "2");
        registerParams.put("distance", "40");

        return executePost("/lines", registerParams);
    }

    private ExtractableResponse<Response> saveLine2() {
        Map<String, String> stationRegisterParam1 = new HashMap<>();
        stationRegisterParam1.put("name", "을지로입구");
        executePost("/stations", stationRegisterParam1);

        Map<String, String> stationRegisterParam2 = new HashMap<>();
        stationRegisterParam2.put("name", "신도림");
        executePost("/stations", stationRegisterParam2);

        Map<String, String> registerParams = new HashMap<>();
        registerParams.put("name", "2호선");
        registerParams.put("color", "green");
        registerParams.put("upStationId", "3");
        registerParams.put("downStationId", "4");
        registerParams.put("distance", "40");

        return executePost("/lines", registerParams);
    }
}
