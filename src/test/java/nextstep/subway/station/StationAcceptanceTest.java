package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.StationStep.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    private static StationRequest 강남역 = station("강남역");
    private static StationRequest 역삼역 = station("역삼역");

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

        // then
        지하철역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청(강남역);

        // then
        지하철역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1 = 지하철역_생성_요청(강남역);
        ExtractableResponse<Response> createResponse2 = 지하철역_생성_요청(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철_조회_요청();

        // then
        지하철역_조회됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_제거_요청(createResponse.header("Location"));

        // then
        지하철역_제거됨(response);
    }

    private static StationRequest station(String name) {
        return new StationRequest(name);
    }
}
