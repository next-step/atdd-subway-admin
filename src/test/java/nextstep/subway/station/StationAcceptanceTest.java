package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceStep.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    private static final StationRequest 강남역 = new StationRequest("강남역");
    private static final StationRequest 역삼역 = new StationRequest("역삼역");
    private static final StationRequest 판교역 = new StationRequest("판교역");


    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);
        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(강남역);
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);
        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // given
        Long createdStationId1 = 지하철_역_등록되어_있음(강남역);
        Long createdStationId2 = 지하철_역_등록되어_있음(역삼역);
        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();
        // then
        지하철_역_목록_응답됨(response);
        지하철_역_목록_포함됨(createdStationId1, createdStationId2, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        Long createdStationId = 지하철_역_등록되어_있음(강남역);
        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(createdStationId);
        // then
        지하철_역_삭제됨(response);
    }
}
