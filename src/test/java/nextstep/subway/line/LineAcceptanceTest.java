package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.LineAcceptanceTestRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.utils.LineAcceptanceTestRequest.*;
import static nextstep.subway.utils.LineAcceptanceTestResponse.*;
import static nextstep.subway.utils.StationTestCommon.지하철_역_생성됨;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("구간을 포함한 지하철 노선을 생성한다.")
    @Test
    void newCreateLine() {
        // given
        StationResponse upStation = 지하철_역_생성됨("서울대입구역");
        StationResponse downStation = 지하철_역_생성됨("사당역");
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10L);

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(params, "/lines");

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        StationResponse upStation = 지하철_역_생성됨("서울대입구역");
        StationResponse downStation = 지하철_역_생성됨("사당역");
        LineRequest lineRequest = 지하철_노선_요청_생성("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10L);
        지하철_노선_등록되어_있음(lineRequest, "/lines");

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(lineRequest, "/lines");

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선을 생성할때 종점역(상행, 하행)을 함께 추가한다.")
    @Test
    void createLineWithStations() {
        // given
        StationResponse upStation = 지하철_역_생성됨("서울대입구역");
        StationResponse downStation = 지하철_역_생성됨("사당역");
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", upStation.getId(), downStation.getId(), 10L);

        // when
        ExtractableResponse<Response> response = 지하철_노선_등록되어_있음(params, "/lines");

        // then
        지하철_노선_생성됨(upStation.getId(), downStation.getId(), response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        StationResponse upStation_1 = 지하철_역_생성됨("서울대입구역");
        StationResponse downStation_1 = 지하철_역_생성됨("사당역");
        LineRequest params1 = 지하철_노선_요청_생성("신분당선", "bg-red-600", upStation_1.getId(), downStation_1.getId(), 10L);

        StationResponse upStation_2 = 지하철_역_생성됨("선릉역");
        StationResponse downStation_2 = 지하철_역_생성됨("삼성역");
        LineRequest params2 = 지하철_노선_요청_생성("2호선", "bg-green-600", upStation_2.getId(), downStation_2.getId(), 10L);

        // when
        ExtractableResponse<Response> createResponse1 = 지하철_노선_등록되어_있음(params1, "/lines");
        ExtractableResponse<Response> createResponse2 = 지하철_노선_등록되어_있음(params2, "/lines");
        ExtractableResponse<Response> allResponse = 지하철_노선_목록_조회_요청("/lines");

        // then
        List<Long> expectedLineIds = Stream.of(createResponse1, createResponse2)
                .map(it -> Long.parseLong(it.header("Location").split("/")[2]))
                .collect(Collectors.toList());

        List<Long> resultLineIds = allResponse.jsonPath()
                .getList(".", LineResponse.class).stream()
                .map(it -> it.getId())
                .collect(Collectors.toList());

        assertThat(resultLineIds).containsAll(expectedLineIds);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        StationResponse upStation_1 = 지하철_역_생성됨("서울대입구역");
        StationResponse downStation_1 = 지하철_역_생성됨("사당역");
        LineRequest params = 지하철_노선_요청_생성("신분당선", "bg-red-600", upStation_1.getId(), downStation_1.getId(), 10L);
        지하철_노선_등록되어_있음(params, "/lines");

        // when
        Long id = 1L;
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines/" + id);

        // then
        지하철_노선_조회됨(response, params);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        StationResponse upStation_1 = 지하철_역_생성됨("서울대입구역");
        StationResponse downStation_1 = 지하철_역_생성됨("사당역");
        LineRequest createParam = 지하철_노선_요청_생성("신분당선", "bg-red-600", upStation_1.getId(), downStation_1.getId(), 10L);
        지하철_노선_등록되어_있음(createParam, "/lines");

        StationResponse upStation_2 = 지하철_역_생성됨("선릉역");
        StationResponse downStation_2 = 지하철_역_생성됨("삼성역");
        LineRequest updateParam = 지하철_노선_요청_생성("2호선", "bg-green-600", upStation_2.getId(), downStation_2.getId(), 10L);

        // when
        Long id = 1L;
        ExtractableResponse<Response> response = LineAcceptanceTestRequest.지하철_노선_수정_요청(updateParam,"/lines/" + id);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        StationResponse upStation_1 = 지하철_역_생성됨("서울대입구역");
        StationResponse downStation_1 = 지하철_역_생성됨("사당역");
        LineRequest createParam = 지하철_노선_요청_생성("신분당선", "bg-red-600", upStation_1.getId(), downStation_1.getId(), 10L);
        지하철_노선_등록되어_있음(createParam, "/lines");

        // when
        Long id = 1L;
        ExtractableResponse<Response> response = 지하철_노선_제거_요청("/lines/" + id);
        // then
        지하철_노선_삭제됨(response);
    }

}
