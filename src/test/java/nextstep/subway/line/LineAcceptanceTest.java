package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.LineResponses;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    final static private String uri = "/lines";

    private StationAcceptanceTest stationAcceptanceTest = new StationAcceptanceTest();
    private List<Long> stationIds = new ArrayList<>();

    @BeforeEach
    public void setup() {
        String[] stationNames = new String[]{"강남역", "역삼역", "선릉역", "삼성역"};
        for (String stationName : stationNames) {
            StationResponse stationResponse = stationAcceptanceTest.지하철_역_등록되어_있음(new StationRequest(stationName));
            stationIds.add(stationResponse.getId());
        }
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 데이터_생성_요청(lineRequest, uri);
        LineResponse lineResponse = response.as(LineResponse.class);

        // then
        // 지하철_노선_생성됨
        데이터_생성됨(response);
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10);

        // given
        // 지하철_노선_등록되어_있음
        지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> response = 데이터_생성_요청(lineRequest, uri);

        // then
        // 지하철_노선_생성_실패됨
        데이터_생성_실패됨(response, "중복된 노선을 추가할 수 없습니다.");
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(lineRequest);
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest2 = new LineRequest("2호선", "bg-green-600", stationIds.get(2), stationIds.get(3), 10);
        LineResponse lineResponse2 = 지하철_노선_등록되어_있음(lineRequest2);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = 목록_조회_요청(uri);

        // then
        // 지하철_노선_목록_응답됨
        목록_응답됨(response);
        // 지하철_노선_목록_포함됨
        LineResponses lineResponses = response.as(LineResponses.class);
        목록에_포함_검증(lineResponses, lineResponse, lineResponse2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        // 지하철_노선_응답됨
        목록_응답됨(response);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return RestAssured.given()
                .log().all()
                .when()
                .get(String.format("/lines/%s", id))
                .then()
                .log().all()
                .extract();
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10);
        LineResponse lineResponse = 지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선_수정_요청
        LineRequest updateLineRequest = new LineRequest("구분당선", "bg-blue-600", stationIds.get(2), stationIds.get(3), 10);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse.getId(), updateLineRequest);
        LineResponse updatedLine = response.as(LineResponse.class);

        // then
        // 지하철_노선_수정됨
        지하철_노선_수정됨(lineResponse, updatedLine);
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, LineRequest lineRequest) {
        return RestAssured.given()
                .log().all()
                .body(lineRequest)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .put(String.format("/lines/%s", id))
                .then()
                .log().all()
                .extract();
    }

    private void 지하철_노선_수정됨(LineResponse lineResponse, LineResponse updatedLine) {
        assertAll(
                () -> assertThat(updatedLine.getId().equals(lineResponse.getId())).isTrue(),
                () -> assertThat(updatedLine.getName().equals(lineResponse.getName())).isFalse(),
                () -> assertThat(updatedLine.getColor().equals(lineResponse.getColor())).isFalse(),
                () -> assertThat(updatedLine.getStations().equals(lineResponse.getStations())).isFalse()
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", stationIds.get(0), stationIds.get(1), 10);
        ExtractableResponse<Response> createResponse = 데이터_생성_요청(lineRequest, uri);

        // when
        // 지하철_노선_제거_요청
        String deleteUri = createResponse.header("Location");
        ExtractableResponse<Response> response = 데이터_제거_요청(deleteUri);

        // then
        // 지하철_노선_삭제됨
        데이터_삭제완료됨(response);
    }

    private LineResponse 지하철_노선_등록되어_있음(final LineRequest request) {
        ExtractableResponse<Response> response = 데이터_생성_요청(request, uri);
        return response.as(LineResponse.class);
    }

}
