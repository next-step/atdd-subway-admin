package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineFixture.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private LineResponse line2;

    private StationResponse upStation;

    private StationResponse downStation;

    @BeforeEach
    void initStations() {
        super.setUp();

        Map<String, String> params1 = new HashMap<>();
        params1.put("name", "강남역");
        Map<String, String> params2 = new HashMap<>();
        params2.put("name", "합정역");
        Map<String, String> params3 = new HashMap<>();
        params3.put("name", "교대역");

        upStation = StationAcceptanceTest.지하철_역_생성_요청(params1)
                .as(StationResponse.class);
        downStation = StationAcceptanceTest.지하철_역_생성_요청(params2)
                .as(StationResponse.class);

        LineRequest request = new LineRequest("2호선", "green", upStation.getId(), downStation.getId(), 100);
        line2 = 지하철_노선_생성_요청(request).as(LineResponse.class);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        String name = "신분당선";
        String color = "red";
        LineRequest lineRequest = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_응답됨(response);
        지하철_노선_값_검증됨(response, name, color);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        String name = "신분당선";
        String color = "red";
        LineRequest lineRequest = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        지하철_노선_생성_요청(lineRequest);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(lineRequest);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineRequest lineRequest1 = LineRequest.builder()
                .name("신분당선")
                .color("red")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        LineRequest lineRequest2 = LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse1 = 지하철_노선_생성_요청(lineRequest1);
        ExtractableResponse<Response> createdResponse2 = 지하철_노선_생성_요청(lineRequest2);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_응답됨(response);
        지하철_노선_목록_포함됨(response, createdResponse1, createdResponse2);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        String name = "신분당선";
        String color = "red";
        LineRequest lineRequest = LineRequest.builder()
                .name(name)
                .color(color)
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(lineRequest);

        // when
        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_값_검증됨(response, name, color);
    }

    @DisplayName("존재하지 않는 지하철 노선을 조회한다.")
    @Test
    void getLineFail() {
        // give
        String uri = "lines/0";

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(uri);

        // then
        지하철_노선_조회_실패_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineRequest createRequest = LineRequest.builder()
                .name("신분당선")
                .color("red")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(createRequest);
        LineRequest updateRequest = LineRequest.builder()
                .name("1호선")
                .color("blue")
                .upStationId(2L)
                .downStationId(1L)
                .distance(5)
                .build();

        // when
        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(uri, updateRequest);

        // then
        지하철_노선_응답됨(response);
        지하철_노선_값_검증됨(response, updateRequest.getName(), updateRequest.getColor());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineRequest createRequest = LineRequest.builder()
                .name("신분당선")
                .color("red")
                .upStationId(1L)
                .downStationId(2L)
                .distance(10)
                .build();
        ExtractableResponse<Response> createdResponse = 지하철_노선_생성_요청(createRequest);

        // when
        String uri = createdResponse.header("Location");
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(uri);

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("지하철 노선의 역 사이에 새로운 역을 등록한다.")
    @Test
    void createStationBetween() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "교대역");
        StationResponse newStation = StationAcceptanceTest.지하철_역_생성_요청(params).as(StationResponse.class);

        // when
        SectionRequest request = new SectionRequest(upStation.getId(), newStation.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(line2.getId(), request);

        // then
        지하철_노선_응답됨(response);
        지하철_노선에_지하철역_포함됨(response, newStation.getId());
    }

    @DisplayName("지하철 노선에 새로운 역을 상행 종점으로 등록한다.")
    @Test
    void createStationUp() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "역삼역");
        StationResponse newStation = StationAcceptanceTest.지하철_역_생성_요청(params).as(StationResponse.class);

        // when
        SectionRequest request = new SectionRequest(newStation.getId(), upStation.getId(), 20);
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(line2.getId(), request);

        // then
        지하철_노선_응답됨(response);
        지하철_노선에_지하철역_포함됨(response, newStation.getId());
    }

    @DisplayName("지하철 노선에 새로운 역을 하행 종점으로 등록한다.")
    @Test
    void createStationDown() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "홍대입구역");
        StationResponse newStation = StationAcceptanceTest.지하철_역_생성_요청(params).as(StationResponse.class);

        // when
        SectionRequest request = new SectionRequest(downStation.getId(), newStation.getId(), 15);
        ExtractableResponse<Response> response = 지하철_노선에_지하철역_등록_요청(line2.getId(), request);

        // then
        지하철_노선_응답됨(response);
        지하철_노선에_지하철역_포함됨(response, newStation.getId());
    }
}
