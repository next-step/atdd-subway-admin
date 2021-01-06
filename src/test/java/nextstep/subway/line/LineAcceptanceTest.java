package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTestSupport;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String upStationName = "강남역";
        String downStationName = "역삼역";
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                                    , upStationName, downStationName, 10);

        // then
        // 지하철_노선_생성됨
        LineResponse lineResponse = response.as(LineResponse.class);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
        assertThat(lineResponse.getStations().stream().map(StationResponse::getName)).contains(upStationName, downStationName);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , "강남역", "역삼역", 10);

        // when
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , "양재역", "판교역", 10);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> createResponse1
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , "강남역", "역삼역", 10);
        ExtractableResponse<Response> createResponse2
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("4호선", "bg-blue-400"
                , "양재역", "판교역", 20);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceTestSupport.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> results = response.jsonPath().getList(".", LineResponse.class);
        assertThat(results).contains(createResponse1.as(LineResponse.class), createResponse2.as(LineResponse.class));

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , "강남역", "역삼역", 10);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_조회_요청(createResponse1.header(HttpHeaders.LOCATION));

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse createResponse = createResponse1.as(LineResponse.class);
        LineResponse findResponse = response.as(LineResponse.class);
        assertEquals(createResponse.getId(), findResponse.getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , "강남역", "역삼역", 10);
        String updateLineName = "구분당선";

        // when
        // 지하철_노선_수정_요청
        LineRequest modifiedLineRequest = new LineRequest(updateLineName, "bg-red-600"
                , LineAcceptanceTestSupport.getUpStationId(), LineAcceptanceTestSupport.getDownStationId()
                , 10);
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_수정_요청(modifiedLineRequest
                                                , createResponse1.header(HttpHeaders.LOCATION));
        LineResponse findUpdateLineResponse
                = LineAcceptanceTestSupport.지하철_노선_조회_요청(createResponse1.header(HttpHeaders.LOCATION))
                    .as(LineResponse.class);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findUpdateLineResponse.getName()).isEqualTo(updateLineName);
    }

    @DisplayName("ID가 없는 노선을 수정 요청 하는 경우 오류 발생")
    @Test
    void updateLineByNotExistIdOccurredException() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , "강남역", "역삼역", 10);

        // when
        // 지하철_노선_수정_요청
        LineRequest modifiedLineRequest = new LineRequest("구분당선", "bg-red-600"
                , LineAcceptanceTestSupport.getUpStationId(), LineAcceptanceTestSupport.getDownStationId()
                , 10);
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_수정_요청(modifiedLineRequest
                , createResponse1.header(HttpHeaders.LOCATION) + "00");

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
                = LineAcceptanceTestSupport.지하철_노선_생성_요청("신분당선", "bg-red-600"
                , "강남역", "역삼역", 10);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response
                = LineAcceptanceTestSupport.지하철_노선_제거_요청(createResponse1.header(HttpHeaders.LOCATION));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
