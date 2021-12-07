package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.dto.StationResponses;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    final static private String uri = "/stations";

    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");

        // when
        ExtractableResponse<Response> response = 데이터_생성_요청(stationRequest, uri);

        // then
        데이터_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        StationRequest stationRequest = new StationRequest("강남역");

        // given
        지하철_역_등록되어_있음(stationRequest);

        // when
        ExtractableResponse<Response> response = 데이터_생성_요청(stationRequest, uri);

        // then
        데이터_생성_실패됨(response, "중복된 역을 추가할 수 없습니다.");
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        StationRequest 강남역_요청 = new StationRequest("강남역");
        StationResponse 강남역 = 지하철_역_등록되어_있음(강남역_요청);

        StationRequest 역삼역_요청 = new StationRequest("역삼역");
        StationResponse 역삼역 = 지하철_역_등록되어_있음(역삼역_요청);

        // when
        ExtractableResponse<Response> response = 목록_조회_요청(uri);

        // then
        목록_응답됨(response);
        StationResponses stationResponses = response.as(StationResponses.class);
        목록에_포함_검증(stationResponses, 강남역, 역삼역);
    }

    public StationResponse 지하철_역_등록되어_있음(final StationRequest request) {
        ExtractableResponse<Response> response = 데이터_생성_요청(request, uri);
        return response.as(StationResponse.class);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        StationRequest stationRequest = new StationRequest("강남역");
        ExtractableResponse<Response> createResponse = 데이터_생성_요청(stationRequest, uri);

        // when
        String deleteUri = createResponse.header("Location");
        ExtractableResponse<Response> response = 데이터_제거_요청(deleteUri);

        // then
        데이터_삭제완료됨(response);
    }

}
