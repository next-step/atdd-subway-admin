package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationFixture;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철 노선을 구역과 함께 생성한다.")
    @Test
    void createLineWithSection() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response =
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green");

        // then
        // 지하철_노선_생성됨
        checkResponseStatus(response, HttpStatus.CREATED);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineFixture.requestCreateLine("2호선", "green");

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineFixture.requestCreateLine("2호선", "green");

        // then
        // 지하철_노선_생성_실패됨
        checkResponseStatus(response, HttpStatus.BAD_REQUEST);
    }

    @DisplayName("지하철 노선 목록을 구간과 함께 조회한다.")
    @Test
    void getLinesWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse1 =
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green");
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createdResponse2 =
                requestCreateLineWithStation("신사역", "삼성역", 6, "3호선", "orange");

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineFixture.requestGetLines();

        // then
        // 지하철_노선_목록_응답됨
        checkResponseStatus(response, HttpStatus.OK);
        // 지하철_노선_목록_포함됨
        checkContainsResponses(createdResponse1, createdResponse2, response);
    }

    @DisplayName("지하철 노선을 구간과 함께 조회한다.")
    @Test
    void getLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = LineFixture.ofLineResponse(
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green")
        );

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineFixture.requestGetLineById(createdLineResponse.getId());

        // then
        // 지하철_노선_응답됨
        checkResponseStatus(response, HttpStatus.OK);
    }

    @DisplayName("지하철 노선을 구역과 함께 수정한다.")
    @Test
    void updateLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = LineFixture.ofLineResponse(
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green")
        );
        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response =
                requestUpdateLineWithStation(createdLineResponse.getId(), "신사역", "삼성역", 3, "3호선", "orange");

        // then
        // 지하철_노선_생성됨
        checkResponseStatus(response, HttpStatus.OK);
    }


    @DisplayName("지하철 노선을 구간과 함께 제거한다.")
    @Test
    void deleteLineWithSection() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = LineFixture.ofLineResponse(
                requestCreateLineWithStation("역삼역", "강남역", 9, "2호선", "green")
        );

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = LineFixture.requestDeleteLine(createdLineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        checkResponseStatus(response, HttpStatus.NO_CONTENT);

        ExtractableResponse<Response> getResponse = LineFixture.requestGetLineById(createdLineResponse.getId());
        checkResponseStatus(getResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ExtractableResponse<Response> requestCreateLineWithStation(String upStationName, String downStationName, int distance, String lineName, String colorName) {
        Station upStation = StationFixture.ofStation(StationFixture.requestCreateStations(upStationName));
        Station downStation = StationFixture.ofStation(StationFixture.requestCreateStations(downStationName));
        Map<String, String> params = LineFixture.createParams(lineName, colorName, upStation.getId(), downStation.getId(), distance);
        return LineFixture.requestCreateLine(params);
    }


    private ExtractableResponse<Response> requestUpdateLineWithStation(Long lineId, String upStationName, String downStationName, int distance, String lineName, String colorName) {
        Station upStation = StationFixture.ofStation(StationFixture.requestCreateStations(upStationName));
        Station downStation = StationFixture.ofStation(StationFixture.requestCreateStations(downStationName));
        Map<String, String> params = LineFixture.createParams(lineName, colorName, upStation.getId(), downStation.getId(), distance);
        return LineFixture.requestUpdateLine(lineId, params);
    }

    private void checkContainsResponses(ExtractableResponse<Response> createdResponse1, ExtractableResponse<Response> createdResponse2, ExtractableResponse<Response> response) {
        List<LineResponse> expectedLines = Arrays.asList(LineFixture.ofLineResponse(createdResponse1), LineFixture.ofLineResponse(createdResponse2));
        List<LineResponse> lines = LineFixture.ofLineResponses(response);
        assertThat(lines).containsAll(expectedLines);
    }

    private void checkResponseStatus(ExtractableResponse<Response> response, HttpStatus status) {
        assertThat(response.statusCode()).isEqualTo(status.value());
    }
}
