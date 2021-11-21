package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.StationScenarioMethod.*;

@DisplayName("지하철 역 관련 기능")
class StationAcceptanceTest extends AcceptanceTest {

    private StationRequest 강남역 = new StationRequest("강남역");
    private StationRequest 역삼역 = new StationRequest("역삼역");
    private StationRequest 몽촌토성역 = new StationRequest("몽촌토성역");

    @DisplayName("지하철 역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("지하철 역이 이미 등록되어 있는 경우 지하철 역 생성에 실패한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(강남역);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남역);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철 역 목록을 조회한다.")
    @Test
    void findStations() {
        // given
        지하철_역_등록되어_있음(강남역);
        지하철_역_등록되어_있음(역삼역);

        // when
        ExtractableResponse<Response> response = 지하철_역_조회_요청("/stations");

        // then
        지하철_역_조회_응답됨(response);
        지하철_역_목록_조회_결과_포함됨(response, 강남역);
        지하철_역_목록_조회_결과_포함됨(response, 역삼역);
    }

    @DisplayName("지하철 역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        String createdLocationUri = 지하철_역_등록되어_있음(몽촌토성역);

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(createdLocationUri);

        // then
        지하철_역_삭제됨(response);
    }
}
