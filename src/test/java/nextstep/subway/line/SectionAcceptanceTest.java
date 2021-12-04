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
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실역, 10);

        // then
        TestSectionFactory.지하철_노선에_구간_등록됨(response);

        // when
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(이호선.getId());

        // then
        TestLineFactory.지하철_노선_응답됨(lineResponse);
        TestSectionFactory.지하철_노선에_지하철역_구간_목록_포함됨(lineResponse, 3);

    }

    @DisplayName("노선에 새로운 하행 종점을 등록한다.")
    @Test
    void addDownSection() {
        // given
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 강변역 = TestStationFactory.지하철_역_생성_요청("강변역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실나루역, 강변역, 10);

        // then
        TestSectionFactory.지하철_노선에_구간_등록됨(response);

        // when
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(이호선.getId());

        // then
        TestLineFactory.지하철_노선_응답됨(lineResponse);
        TestSectionFactory.지하철_노선에_지하철역_구간_목록_포함됨(lineResponse, 3);
    }

    @DisplayName("노선 사이에 역을 추가한다.")
    @Test
    void addMiddleSection() {
        // given
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실역, 5);

        // then
        TestSectionFactory.지하철_노선에_구간_등록됨(response);

        // when
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(이호선.getId());

        // then
        TestLineFactory.지하철_노선_응답됨(lineResponse);
        TestSectionFactory.지하철_노선에_지하철역_구간_목록_포함됨(lineResponse, 3);
    }

    @DisplayName("노선 사이에 역을 추가할 시 길이가 똑같으면 등록 실패한다.")
    @Test
    void addMiddleSection_예외() {
        // given
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실역, 10);

        // then
        TestSectionFactory.지하철_노선에_지하철역_구간_생성_실패됨(response);
    }

    @DisplayName("노선에 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 실패한다.")
    @Test
    void addUpDownSection_예외() {
        // given
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);

        // when
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실새내역, 잠실나루역, 10);

        // then
        TestSectionFactory.지하철_노선에_지하철역_구간_생성_실패됨(response);
    }

    @DisplayName("노선에 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 실패한다.")
    @Test
    void addUpDownSection_예외2() {
        // given
        StationResponse 잠실새내역 = TestStationFactory.지하철_역_생성_요청("잠실새내역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실새내역.getId()
                , 잠실나루역.getId(), 10)).as(LineResponse.class);

        // when
        StationResponse 강변역 = TestStationFactory.지하철_역_생성_요청("강변역").as(StationResponse.class);
        StationResponse 종합운동장역 = TestStationFactory.지하철_역_생성_요청("종합운동장역").as(StationResponse.class);
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 강변역, 종합운동장역, 10);

        // then
        TestSectionFactory.지하철_노선에_지하철역_구간_생성_실패됨(response);
    }

    @DisplayName("노선에 종점이 제거되면 다음으로 오던 역이 종점이 된다.")
    @Test
    void removeMiddleSection() {
        // given
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실역.getId()
                , 잠실나루역.getId(), 5)).as(LineResponse.class);
        StationResponse 강변역 = TestStationFactory.지하철_역_생성_요청("강변역").as(StationResponse.class);

        // when
        ExtractableResponse<Response> response = TestSectionFactory.지하철_노선에_구간_등록_요청(이호선, 잠실나루역, 강변역, 5);

        // then
        TestSectionFactory.지하철_노선에_구간_등록됨(response);

        // when
        ExtractableResponse<Response> removeResponse = TestSectionFactory.지하철_노선_구간_삭제_요청(이호선, 잠실나루역);

        // then
        TestSectionFactory.지하철_노선_구간_삭제됨(removeResponse);

        // when
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(이호선.getId());

        // then
        TestLineFactory.지하철_노선_응답됨(lineResponse);
        TestSectionFactory.지하철_노선에_지하철역_구간_목록_포함됨(lineResponse, 2);
    }

    @DisplayName("노선에 구간이 하나인 경우 삭제가 불가능하다.")
    @Test
    void removeSection_예외() {
        // given
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실역.getId()
                , 잠실나루역.getId(), 5)).as(LineResponse.class);

        // when
        ExtractableResponse<Response> removeResponse = TestSectionFactory.지하철_노선_구간_삭제_요청(이호선, 잠실나루역);

        // then
        TestSectionFactory.지하철_노선_구간_삭제_실패됨(removeResponse);
    }

    @DisplayName("삭제할 역이 노선에 없는 경우 삭제가 불가능하다.")
    @Test
    void removeSection_예외3() {
        // given
        StationResponse 잠실역 = TestStationFactory.지하철_역_생성_요청("잠실역").as(StationResponse.class);
        StationResponse 잠실나루역 = TestStationFactory.지하철_역_생성_요청("잠실나루역").as(StationResponse.class);
        LineResponse 이호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("2호선", "bg-red-600", 잠실역.getId()
                , 잠실나루역.getId(), 5)).as(LineResponse.class);
        // 다른 노선
        StationResponse 몽촌토성역 = TestStationFactory.지하철_역_생성_요청("몽촌토성역").as(StationResponse.class);
        StationResponse 강동구청역 = TestStationFactory.지하철_역_생성_요청("강동구청역").as(StationResponse.class);
        LineResponse 팔호선 = TestLineFactory.지하철_노선_생성_요청(LineRequest.of("8호선", "bg-green-600", 몽촌토성역.getId()
                , 강동구청역.getId(), 5)).as(LineResponse.class);

        // when
        ExtractableResponse<Response> removeResponse = TestSectionFactory.지하철_노선_구간_삭제_요청(이호선, 몽촌토성역);

        // then
        TestSectionFactory.지하철_노선_구간_삭제_실패됨(removeResponse);
    }
}
