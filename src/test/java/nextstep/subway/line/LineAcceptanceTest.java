package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.utils.RequestTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse upStation;
    private StationResponse downStation;

    @BeforeEach
    public void setUp() {
        super.setUp();
        // given
        // 지하철_역_등록_되어있음
        upStation = StationAcceptanceTest.createRequest(new StationRequest("신도림"))
                .as(StationResponse.class);
        downStation = StationAcceptanceTest.createRequest(new StationRequest("까치산"))
                .as(StationResponse.class);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createRequest(new LineRequest("2호선", "초록", upStation.getId(), downStation.getId(), 10));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("등록되지 않은 역으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithNotRegisterStation() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createRequest(new LineRequest("2호선", "초록", 100L, 200L, 10));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행, 하행 역이 같은 지하철 노선을 생성한다.")
    @Test
    void createLineWithSameStation() {
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createRequest(new LineRequest("2호선", "초록", upStation.getId(), upStation.getId(), 10));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행, 하행 역이 이미 등록 된 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateStation() {
        // given
        // 지하철_노선_생성_되어있음
        createRequest(new LineRequest("1호선", "초록", upStation.getId(), upStation.getId(), 10));

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createRequest(new LineRequest("2호선", "초록", upStation.getId(), upStation.getId(), 10));

        // then
        // 지하철_노선_생성됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineDuplicate() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("2호선", "초록", upStation.getId(), downStation.getId(), 10);
        createRequest(lineRequest);
        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = createRequest(lineRequest);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_역_등록되어_있음
        StationResponse otherDownStation = StationAcceptanceTest.createRequest(new StationRequest("구로"))
                .as(StationResponse.class);
        // 지하철_노선_등록되어_있음
        LineResponse createResponse = createRequest(new LineRequest("2호선", "초록", upStation.getId(), downStation.getId(), 10))
                .as(LineResponse.class);
        LineResponse createResponse2 = createRequest(new LineRequest("1호선", "파랑", upStation.getId(), otherDownStation.getId(), 10))
                .as(LineResponse.class);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = selectAllRequest();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> result = response.jsonPath()
                .getList(".", LineResponse.class);
        assertThat(result).contains(createResponse, createResponse2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse createdLineResponse = createRequest(new LineRequest("2호선", "초록", upStation.getId(), downStation.getId(), 10))
                .as(LineResponse.class);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = selectRequestWithId(createdLineResponse.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_정보_확인
        LineResponse selectedLineResponse = response.as(LineResponse.class);
        assertThat(selectedLineResponse).isEqualTo(createdLineResponse);

        List<String> stationNames = selectedLineResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(stationNames).containsExactly(upStation.getName(), downStation.getName());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse lineResponse = createRequest(new LineRequest("2호선", "초록", upStation.getId(), downStation.getId(), 10))
                .as(LineResponse.class);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response = updateRequest(lineResponse.getId(), new LineRequest("2호선", "빨강", upStation.getId(), downStation.getId(), 10));

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse updatedResponse = response.as(LineResponse.class);
        assertThat(updatedResponse.getName()).isEqualTo("2호선");
        assertThat(updatedResponse.getColor()).isEqualTo("빨강");
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse lineResponse = createRequest(new LineRequest("2호선", "초록", upStation.getId(), downStation.getId(), 10))
                .as(LineResponse.class);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = deleteRequest(lineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public static ExtractableResponse<Response> createRequest(LineRequest lineRequest) {
        final String url = "/lines";
        return RequestTest.doPost(url, lineRequest);
    }

    public static ExtractableResponse<Response> selectRequestWithId(Long lineId) {
        final String url = "/lines/" + lineId;
        return RequestTest.doGet(url);
    }

    private ExtractableResponse<Response> selectAllRequest() {
        final String url = "/lines";
        return RequestTest.doGet(url);
    }

    private ExtractableResponse<Response> updateRequest(Long lineId, LineRequest lineRequest) {
        final String url = "/lines/" + lineId;
        return RequestTest.doPut(url, lineRequest);
    }

    private ExtractableResponse<Response> deleteRequest(Long lineId) {
        final String url = "/lines/" + lineId;
        return RequestTest.doDelete(url);
    }
}
