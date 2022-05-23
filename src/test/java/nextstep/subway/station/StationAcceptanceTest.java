package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_목록_응답;
import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_목록_조회;
import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_목록에_포함;
import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_생성;
import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_생성_실패;
import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_생성됨;
import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_제거;
import static nextstep.subway.station.StationAcceptanceTestMethods.지하철역_제거됨;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> 강남역_생성_response = 지하철역_생성(StationRequest.from("강남역"));

        // then
        지하철역_생성됨(강남역_생성_response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성(StationRequest.from("강남역"));

        // when
        ExtractableResponse<Response> 강남역_생성_response = 지하철역_생성(StationRequest.from("강남역"));

        // then
        지하철역_생성_실패(강남역_생성_response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        ExtractableResponse<Response> 판교역_생성_response = 지하철역_생성(StationRequest.from("판교역"));
        ExtractableResponse<Response> 강남역_생성_response = 지하철역_생성(StationRequest.from("강남역"));

        // when
        ExtractableResponse<Response> 목록_조회_response = 지하철역_목록_조회();

        // then
        지하철역_목록_응답(목록_조회_response);
        지하철역_목록에_포함(목록_조회_response, Arrays.asList(판교역_생성_response, 강남역_생성_response));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> 판교역_생성_response = 지하철역_생성(StationRequest.from("판교역"));

        // when
        ExtractableResponse<Response> 판교역_제거_response = 지하철역_제거(판교역_생성_response);

        // then
        지하철역_제거됨(판교역_제거_response);
    }
}
