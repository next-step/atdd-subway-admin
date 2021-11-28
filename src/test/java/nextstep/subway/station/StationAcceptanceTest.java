package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.station.StationScenarioMethod.*;

@DisplayName("지하철 역 인수 테스트")
class StationAcceptanceTest extends AcceptanceTest {

    private StationRequest 강남 = new StationRequest("강남");
    private StationRequest 양재 = new StationRequest("양재");
    private StationRequest 양재시민의숲 = new StationRequest("양재시민의숲");

    @DisplayName("지하철 역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("지하철 역이 이미 등록되어 있는 경우 지하철 역 생성에 실패한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음(강남);

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(강남);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철 역 목록을 조회한다.")
    @Test
    void findStations() {
        // given
        지하철_역_등록되어_있음(강남);
        지하철_역_등록되어_있음(양재);

        // when
        ExtractableResponse<Response> response = 지하철_역_조회_요청("/stations");

        // then
        지하철_역_조회_응답됨(response);
        지하철_역_목록_조회_결과_포함됨(response, 강남);
        지하철_역_목록_조회_결과_포함됨(response, 양재);
    }

    @DisplayName("지하철 역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        String createdLocationUri = 지하철_역_등록되어_있음(양재시민의숲);

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(createdLocationUri);

        // then
        지하철_역_삭제됨(response);
    }

    @DisplayName("지하철 역이 등록되어 있지 않은 경우 지하철 역 제거가 실패한다.")
    @Test
    void deleteStationValidateEmptyResult() {
        // given
        String notFoundUri = "lines/1";

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(notFoundUri);

        // then
        지하철_역_삭제_실패됨(response);
    }
}
