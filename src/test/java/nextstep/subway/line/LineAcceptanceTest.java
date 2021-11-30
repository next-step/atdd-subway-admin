package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixture.TestLineFactory;
import nextstep.subway.fixture.TestStationFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        TestStationFactory.지하철_역_생성_요청("잠실역");
        TestStationFactory.지하철_역_생성_요청("부평구청역");
        // when
        ExtractableResponse<Response> response = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.LINE_REQUEST);

        // then
        TestLineFactory.지하철_노선_생성됨(response);

        // when
        ExtractableResponse<Response> lineResponse = TestLineFactory.지하철_노선_조회_요청(response);

        // then
        TestLineFactory.지하철_노선_응답됨(lineResponse);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        TestStationFactory.지하철_역_생성_요청("잠실역");
        TestStationFactory.지하철_역_생성_요청("부평구청역");
        ExtractableResponse<Response> createResponse1 = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.LINE_REQUEST);

        // when
        ExtractableResponse<Response> response = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.LINE_REQUEST);

        // then
        TestLineFactory.지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        TestStationFactory.지하철_역_생성_요청("잠실역");
        TestStationFactory.지하철_역_생성_요청("부평구청역");
        ExtractableResponse<Response> createResponse1 = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.LINE_REQUEST);
        TestStationFactory.지하철_역_생성_요청("신도림역");
        TestStationFactory.지하철_역_생성_요청("몽촌토성역");
        ExtractableResponse<Response> createResponse2 = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.ANOTHER_LINE_REQUEST);

        // when
        ExtractableResponse<Response> response = TestLineFactory.지하철들_노선_목록_조회_요청();

        // then
        TestLineFactory.지하철_노선_목록_응답됨(response);
        TestLineFactory.지하철_노선_목록_포함됨(createResponse1, createResponse2, response);

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        TestStationFactory.지하철_역_생성_요청("잠실역");
        TestStationFactory.지하철_역_생성_요청("부평구청역");
        ExtractableResponse<Response> createResponse1 = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.LINE_REQUEST);

        // when
        ExtractableResponse<Response> response = TestLineFactory.지하철_노선_조회_요청(createResponse1);

        // then
        TestLineFactory.지하철_노선_응답됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        TestStationFactory.지하철_역_생성_요청("잠실역");
        TestStationFactory.지하철_역_생성_요청("부평구청역");
        ExtractableResponse<Response> createResponse1 = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.LINE_REQUEST);

        // when
        ExtractableResponse<Response> response = TestLineFactory.지하철_노선_수정_요청(createResponse1, "bg-red-600", "2호선");

        // then
        TestLineFactory.지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        TestStationFactory.지하철_역_생성_요청("잠실역");
        TestStationFactory.지하철_역_생성_요청("부평구청역");
        ExtractableResponse<Response> createResponse1 = TestLineFactory.지하철_노선_생성_요청(TestLineFactory.LINE_REQUEST);

        // when
        ExtractableResponse<Response> response = TestLineFactory.지하철_노선_제거_요청(createResponse1);

        // then
        TestLineFactory.지하철_노선_삭제됨(response);
    }

}
