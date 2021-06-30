package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.LineStepTest.*;
import static nextstep.subway.station.StationAcceptanceTest.TEST_GANGNAM_STATION;
import static nextstep.subway.station.StationAcceptanceTest.TEST_YUCKSAM_STATION;
import static nextstep.subway.station.StationStepTest.지하철_역_등록되어_있음;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public LineRequest testFirstLine;
    public LineRequest testSecondLine;

    private long testKangnamId;
    private long testYucksamId;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();

        testKangnamId = 지하철_역_등록되어_있음(TEST_GANGNAM_STATION);
        testYucksamId = 지하철_역_등록되어_있음(TEST_YUCKSAM_STATION);
        testFirstLine = new LineRequest("1호선", "red", testKangnamId, testYucksamId, 10L);

        long testKachisanId = 지하철_역_등록되어_있음(new StationRequest("까치산역"));
        long testJamsilId = 지하철_역_등록되어_있음(new StationRequest("잠실역"));
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
        long sangamId = 지하철_역_등록되어_있음(new StationRequest("상암역"));
        SectionRequest request = new SectionRequest(testKangnamId, sangamId, 5L);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_추가_요청(firstLineId, request);

        //then
        노선_구간추가_성공_응답됨(response);
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void addSectionWithDuplicateUpAndDownStation() {
        // given
        long firstLineId = 지하철_노선_등록되어_있음(testFirstLine);
        SectionRequest request = new SectionRequest(testKangnamId, testYucksamId, 10L);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_추가_요청(firstLineId, request);

        //then
        구간_추가_실패됨(response);
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void addSectionBiggerDistanceThanExistSection() {
        // given
        long firstLineId = 지하철_노선_등록되어_있음(testFirstLine);
        SectionRequest request = 지하철_구간에_역들이_등록되어_있음("DMC역", "상암역", 40L);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_추가_요청(firstLineId, request);

        //then
        구간_추가_실패됨(response);
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.")
    @Test
    void addSectionNotContains() {
        // given
        long firstLineId = 지하철_노선_등록되어_있음(testFirstLine);
        SectionRequest request = 지하철_구간에_역들이_등록되어_있음("응암역", "이태원역", 10L);

        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_추가_요청(firstLineId, request);

        //then
        구간_추가_실패됨(response);
    }

    @DisplayName("종점이 제거될 경우 다음으로 오던 역이 종점이 된다.")
    @Test
    void removeSection() {
        // given
        long lineId = 지하철_노선_등록되어_있음(testFirstLine);
        long sangamId = 지하철_역_등록되어_있음(new StationRequest("상암역"));
        long dmcId = 지하철_역_등록되어_있음(new StationRequest("DMC역"));

        지하철_구간_등록되어_있음(lineId, new SectionRequest(testYucksamId, sangamId, 5L));
        지하철_구간_등록되어_있음(lineId, new SectionRequest(sangamId, dmcId, 10L));

        // when
        ExtractableResponse<Response> response = 구간_삭제_요청(lineId, sangamId);

        // then
        구간_삭제됨(response);
    }

    @DisplayName("구간 제거 시 구간이 한 개인 경우 제거될 수 없다.")
    @Test
    void removeSingleSection() {
        // given
        long lineId = 지하철_노선_등록되어_있음(testFirstLine);
        long sangamId = 지하철_역_등록되어_있음(new StationRequest("상암역"));

        // when
        ExtractableResponse<Response> response = 구간_삭제_요청(lineId, sangamId);

        // then
        구간_삭제_실패됨(response);
    }
}
