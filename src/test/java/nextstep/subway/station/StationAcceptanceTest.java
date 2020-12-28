package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

@DisplayName("지하철역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = StationAcceptanceTestRequest.지하철역_생성_요청("강남역");

        // then
        StationAcceptanceTestResponse.지하철역_생성_성공(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        StationAcceptanceTestRequest.지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = StationAcceptanceTestRequest.지하철역_생성_요청("강남역");

        // then
        StationAcceptanceTestResponse.지하철역_생성_실패(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = StationAcceptanceTestRequest.지하철역_생성_요청("강남역");
        ExtractableResponse<Response> createResponse2 = StationAcceptanceTestRequest.지하철역_생성_요청("역삼역");

        // when
        ExtractableResponse<Response> response = StationAcceptanceTestRequest.지하철_목록_조회_요청();

        // then
        StationAcceptanceTestResponse.지하철_목록_조회_성공(Arrays.asList(createResponse1, createResponse2), response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = StationAcceptanceTestRequest.지하철역_생성_요청("강남역");
        String uri = createResponse.header("Location");

        // when
        ExtractableResponse<Response> response = StationAcceptanceTestRequest.지하철역_제거_요청(uri);

        // then
        StationAcceptanceTestResponse.지하철역_제거_성공(response);
    }

}
