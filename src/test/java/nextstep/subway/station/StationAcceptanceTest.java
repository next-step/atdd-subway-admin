package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixture.TestStationFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        ExtractableResponse<Response> response = TestStationFactory.지하철_역_생성_요청("잠실역");

        // then
        TestStationFactory.지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        ExtractableResponse<Response> createResponse1 = TestStationFactory.지하철_역_생성_요청("잠실역");

        // when
        ExtractableResponse<Response> response = TestStationFactory.지하철_역_생성_요청("잠실역");

        // then
        TestStationFactory.지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> createResponse1 = TestStationFactory.지하철_역_생성_요청("잠실역");
        ExtractableResponse<Response> createResponse2 = TestStationFactory.지하철_역_생성_요청("부평구청역");

        // when
        ExtractableResponse<Response> response = TestStationFactory.지히철_역들_조회_요청();

        // then
        TestStationFactory.지하철_역_응답됨(response);
        TestStationFactory.지하철_역_목록_포함됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = TestStationFactory.지하철_역_생성_요청("잠실역");

        // when
        ExtractableResponse<Response> response = TestStationFactory.지하철_역_제거_요청(createResponse);

        // then
        TestStationFactory.지하철_역_삭제됨(response);
    }
}
