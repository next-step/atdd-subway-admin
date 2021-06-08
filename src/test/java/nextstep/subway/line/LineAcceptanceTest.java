package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.LineTestCommon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private StationAcceptanceTest stationAcceptanceTest;

    public LineAcceptanceTest(){
        this.stationAcceptanceTest = new StationAcceptanceTest();
    }

    @DisplayName("구간을 포함한 지하철 노선을 생성한다.")
    @Test
    void newCreateLine() {
        // given
        StationResponse upStation = this.stationAcceptanceTest.지하철_역_생성됨("서울대입구역");
        StationResponse downSation = this.stationAcceptanceTest.지하철_역_생성됨("사당역");
        LineRequest params = LineTestCommon.createLineParams("신분당선", "bg-red-600", upStation.getId(), downSation.getId(), 10L);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineTestCommon.createResponse(params, "/lines");

        // then
        // 지하철_노선_생성됨
        LineTestCommon.지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse upStation = this.stationAcceptanceTest.지하철_역_생성됨("서울대입구역");
        StationResponse downSation = this.stationAcceptanceTest.지하철_역_생성됨("사당역");
        LineRequest params = LineTestCommon.createLineParams("신분당선", "bg-red-600", upStation.getId(), downSation.getId(), 10L);
        LineTestCommon.createResponse(params, "/lines");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineTestCommon.createResponse(params, "/lines");

        // then
        // 지하철_노선_생성_실패됨
        LineTestCommon.지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse upStation_1 = this.stationAcceptanceTest.지하철_역_생성됨("서울대입구역");
        StationResponse downSation_1 = this.stationAcceptanceTest.지하철_역_생성됨("사당역");
        LineRequest params1 = LineTestCommon.createLineParams("신분당선", "bg-red-600", upStation_1.getId(), downSation_1.getId(), 10L);

        // 지하철_노선_등록되어_있음
        StationResponse upStation_2 = this.stationAcceptanceTest.지하철_역_생성됨("선릉역");
        StationResponse downSation_2 = this.stationAcceptanceTest.지하철_역_생성됨("삼성역");
        LineRequest params2 = LineTestCommon.createLineParams("2호선", "bg-green-600", upStation_2.getId(), downSation_2.getId(), 10L);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> createResponse1 = LineTestCommon.createResponse(params1, "/lines");
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> createResponse2 = LineTestCommon.createResponse(params2, "/lines");
        // 전체 조회 요청
        ExtractableResponse<Response> allResponse = LineTestCommon.findAllResponse("/lines");

        // then
        // 지하철_노선_목록_포함됨
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
        // 지하철_노선_등록되어_있음
        StationResponse upStation_1 = this.stationAcceptanceTest.지하철_역_생성됨("서울대입구역");
        StationResponse downStation_1 = this.stationAcceptanceTest.지하철_역_생성됨("사당역");
        LineRequest params = LineTestCommon.createLineParams("신분당선", "bg-red-600", upStation_1.getId(), downStation_1.getId(), 10L);
        LineTestCommon.createResponse(params, "/lines");

        // when
        // 지하철_노선_조회_요청
        Long id = 1L;
        ExtractableResponse<Response> extractableResponse = LineTestCommon.findOneResponse(id);

        // then
        // 지하철_노선_조회됨
        LineTestCommon.지하철_노선_조회됨(extractableResponse, params);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse upStation_1 = this.stationAcceptanceTest.지하철_역_생성됨("서울대입구역");
        StationResponse downStation_1 = this.stationAcceptanceTest.지하철_역_생성됨("사당역");
        LineRequest createParam = LineTestCommon.createLineParams("신분당선", "bg-red-600", upStation_1.getId(), downStation_1.getId(), 10L);
        LineTestCommon.createResponse(createParam, "/lines");

        // 지하철_노선_등록되어_있음
        StationResponse upStation_2 = this.stationAcceptanceTest.지하철_역_생성됨("선릉역");
        StationResponse downStation_2 = this.stationAcceptanceTest.지하철_역_생성됨("삼성역");
        LineRequest updateParam = LineTestCommon.createLineParams("2호선", "bg-green-600", upStation_2.getId(), downStation_2.getId(), 10L);

        // when
        // 지하철_노선_조회_요청
        Long id = 1L;
        ExtractableResponse<Response> response = LineTestCommon.updateResponse(updateParam, id);

        // then
        // 지하철_노선_응답됨
        LineTestCommon.지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse upStation_1 = this.stationAcceptanceTest.지하철_역_생성됨("서울대입구역");
        StationResponse downSation_1 = this.stationAcceptanceTest.지하철_역_생성됨("사당역");
        LineRequest createParam = LineTestCommon.createLineParams("신분당선", "bg-red-600", upStation_1.getId(), downSation_1.getId(), 10L);
        LineTestCommon.createResponse(createParam, "/lines");

        // when
        // 지하철_노선_제거_요청
        Long id = 1L;
        ExtractableResponse<Response> response = LineTestCommon.deleteResponse(id);

        // then
        // 지하철_노선_삭제됨
        LineTestCommon.지하철_노선_삭제됨(response);
    }

}
