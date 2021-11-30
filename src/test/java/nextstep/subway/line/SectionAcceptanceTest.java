package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixture.TestLineFactory;
import nextstep.subway.fixture.TestSectionFactory;
import nextstep.subway.fixture.TestStationFactory;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 구간 관련 기능")
class SectionAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선에 새로운 상행 종점을 등록한다.")
    @Test
    void addUpSection() {
        // given
        // 지하철_노선에_지하철역_등록_요청
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);

        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실역, 10);

        // then
        // 지하철_노선에_구간_등록됨
        TestSectionFactory.지하철_노선에_구간_등록됨(response);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(이호선.getId());

        // then
        // 지하철_노선_응답됨
        // 지하철_노선에_지하철역_구간_목록_포함됨
        TestLineFactory.지하철_노선_응답됨(lineResponse);
        TestSectionFactory.지하철_노선에_지하철역_구간_목록_포함됨(lineResponse);

    }

    @DisplayName("노선에 새로운 하행 종점을 등록한다.")
    @Test
    void addDownSection() {
        // given
        // 지하철_노선에_지하철역_등록_요청
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 강변역 = TestStationFactory.지하철_역_생성_요청("강변역").as(StationResponse.class);

        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실나루역, 강변역, 10);

        // then
        // 지하철_노선에_구간_등록됨
        TestSectionFactory.지하철_노선에_구간_등록됨(response);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(이호선.getId());

        // then
        // 지하철_노선_응답됨
        // 지하철_노선에_지하철역_구간_목록_포함됨
        TestLineFactory.지하철_노선_응답됨(lineResponse);
        TestSectionFactory.지하철_노선에_지하철역_구간_목록_포함됨(lineResponse);
    }

    @DisplayName("노선 사이에 역을 추가한다.")
    @Test
    void addMiddleSection() {
        // given
        // 지하철_노선에_지하철역_등록_요청
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);

        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실역, 5);

        // then
        // 지하철_노선에_구간_등록됨
        TestSectionFactory.지하철_노선에_구간_등록됨(response);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(이호선.getId());

        // then
        // 지하철_노선_응답됨
        // 지하철_노선에_지하철역_구간_목록_포함됨
        TestLineFactory.지하철_노선_응답됨(lineResponse);
        TestSectionFactory.지하철_노선에_지하철역_구간_목록_포함됨(lineResponse);
    }

    @DisplayName("노선 사이에 역을 추가할 시 길이가 똑같으면 등록 실패한다.")
    @Test
    void addMiddleSection_예외() {
        // given
        // 지하철_노선에_지하철역_등록_요청
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);

        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실역, 10);

        // then
        // 지하철_노선에_지하철역_구간_생성_실패됨
        TestSectionFactory.지하철_노선에_지하철역_구간_생성_실패됨(response);
    }

    @DisplayName("노선에 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 실패한다.")
    @Test
    void addUpDownSection_예외() {
        // given
        // 지하철_노선에_지하철역_등록_요청
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);

        // when
        // 지하철_노선에_구간_등록_요청
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실나루역, 10);

        // then
        // 지하철_노선에_지하철역_구간_생성_실패됨
        TestSectionFactory.지하철_노선에_지하철역_구간_생성_실패됨(response);
    }

    @DisplayName("노선에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 실패한다.")
    @Test
    void addUpDownSection_예외2() {
        // given
        // 지하철_노선에_지하철역_등록_요청
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);

        // when
        // 지하철_노선에_구간_등록_요청
        StationResponse 강변역 = TestStationFactory.지하철_역_생성_요청("강변역").as(StationResponse.class);
        StationResponse 종합운동장역 = TestStationFactory.지하철_역_생성_요청("종합운동장역").as(StationResponse.class);
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 강변역, 종합운동장역, 10);

        // then
        // 지하철_노선에_지하철역_구간_생성_실패됨
        TestSectionFactory.지하철_노선에_지하철역_구간_생성_실패됨(response);
    }
}
