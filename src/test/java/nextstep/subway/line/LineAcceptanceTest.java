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

import static nextstep.subway.station.StationAcceptanceTest.saveStation;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends RestAcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        // 상행역_하행역_등록되어_있음
        LineResponse station1 = saveStation("광교").jsonPath().getObject(".", LineResponse.class);
        LineResponse station2 = saveStation("강남").jsonPath().getObject(".", LineResponse.class);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = saveLine("신분당선", "red", station1.getId(), station2.getId(), "40");

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
        LineResponse station1 = saveStation("상현").jsonPath().getObject(".", LineResponse.class);
        LineResponse station2 = saveStation("양재").jsonPath().getObject(".", LineResponse.class);
        ExtractableResponse<Response> response = saveLine("신분당선", "red", station1.getId(), station2.getId(), "40");

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
        StationResponse firstStop = lineResponse.getStations().get(0);
        StationResponse lastStop = lineResponse.getStations().get(lineResponse.getStations().size()-1);
        assertAll(
                () -> assertThat(getResponse.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(lineResponse.getName()).isEqualTo("신분당선"),
                () -> assertThat(lineResponse.getColor()).isEqualTo("red"),
                () -> assertThat(lineResponse.getStations().size()).isEqualTo(2),
                () -> assertThat(firstStop.getName()).isEqualTo("광교"),
                () -> assertThat(lastStop.getName()).isEqualTo("강남")
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

    private ExtractableResponse<Response> saveLine(String name, String color, Long upStationId, Long downStationId, String distance) {
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("color", color);
        params.put("upStationId", upStationId.toString());
        params.put("downStationId", downStationId.toString());
        params.put("distance", distance);
        return executePost("/lines", params);
    }

    private ExtractableResponse<Response> saveShinBundangLine() {
        LineResponse upStation = saveStation("강남").jsonPath().getObject(".", LineResponse.class);
        LineResponse downStation = saveStation("광교").jsonPath().getObject(".", LineResponse.class);
        return saveLine("신분당선", "red", upStation.getId(), downStation.getId(), "40");
    }

    private ExtractableResponse<Response> saveLine2() {
        LineResponse upStation = saveStation("을지로입구").jsonPath().getObject(".", LineResponse.class);
        LineResponse downStation = saveStation("신도림").jsonPath().getObject(".", LineResponse.class);
        return saveLine("2호선", "green", upStation.getId(), downStation.getId(), "35");
    }
}
