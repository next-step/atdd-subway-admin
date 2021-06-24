package nextstep.subway.line;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static nextstep.subway.line.LineStepTest.*;
import static nextstep.subway.station.StationAcceptanceTest.TEST_GANGNAM_STATION;
import static nextstep.subway.station.StationAcceptanceTest.TEST_YUCKSAM_STATION;
import static nextstep.subway.station.StationStepTest.지하철_역_등록되어_있음;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public LineRequest testFirstLine;
    public LineRequest testSecondLine;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        long testKangnamId = 지하철_역_등록되어_있음(TEST_GANGNAM_STATION);
        long testYucksamId = 지하철_역_등록되어_있음(TEST_YUCKSAM_STATION);

        long testKachisanId = 지하철_역_등록되어_있음(new StationRequest("까치산역"));
        long testJamsilId = 지하철_역_등록되어_있음(new StationRequest("잠실역"));
        
        testFirstLine = new LineRequest("1호선", "red", testKangnamId, testYucksamId, 10L);
        testSecondLine = new LineRequest("2호선", "blue", testKachisanId, testJamsilId, 20L);
    }

    @DisplayName("두 종점역은 구간의 형태로 관리되는 지하철 노선을 생성한다.")
    @Test
    void createLineWithSection() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(testFirstLine);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        지하철_노선_등록되어_있음(testFirstLine);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(testFirstLine);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        long firstLineId = 지하철_노선_등록되어_있음(testFirstLine);
        long secondLineId = 지하철_노선_등록되어_있음(testSecondLine);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        //then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, firstLineId, secondLineId);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        long createdId = 지하철_노선_등록되어_있음(testFirstLine);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdId);

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_같음(createdId, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        long createdId = 지하철_노선_등록되어_있음(testFirstLine);
        LineRequest parameter = new LineRequest("1호선", "black");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdId, parameter);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        long firstLineId = 지하철_노선_등록되어_있음(testFirstLine);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(firstLineId);

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionBetweenStations() {
        // given
        long firstLineId = 지하철_노선_등록되어_있음(testFirstLine);

        long dmcStationId = 지하철_역_등록되어_있음(new StationRequest("DMC역"));
        long sangamStationId = 지하철_역_등록되어_있음(new StationRequest("상암역"));
        int distance = 10;

        SectionRequest request = new SectionRequest(dmcStationId, sangamStationId, distance);
        // when
        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post(BASE_LINE_URL + "/" + firstLineId + "/sections")
                .then().log().all().extract();

        //then
        지하철_노선_목록_응답됨(response);
    }
}
