package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceMethods.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response
            = 지하철_역_생성_요청("강남역");

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response
            = 지하철_역_생성_요청("강남역");

        // then
        지하철_역_생성_실패(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        ExtractableResponse<Response> createResponse1
            = 지하철_역_등록되어_있음("강남역");
        ExtractableResponse<Response> createResponse2
            = 지하철_역_등록되어_있음("역삼역");

        // when
        ExtractableResponse<Response> response
            = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_응답됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse
            = 지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response
            = 지하철_역_제거_요청(createResponse);

        // then
        지하철_역_제거됨(response);
    }
}
