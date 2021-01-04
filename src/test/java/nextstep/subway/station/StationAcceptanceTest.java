package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.station.StationAcceptanceTestRequest.*;
import static nextstep.subway.utils.HttpTestStatusCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // When
        ExtractableResponse<Response> 생성된_강남역_응답 = 지하철역_생성_요청후_응답("강남역");
        // Then
        지하철_생성_완료(생성된_강남역_응답);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // Given
        String stationName = "강남역";
        지하철역_생성_요청(stationName);
        // When
        ExtractableResponse<Response> 재생성_요청_응답 = 지하철역_재생성_요청(stationName);
        // Then
        지하철역_생성_실패(재생성_요청_응답);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        // Given
        StationResponse 강남역 = 지하철역_생성_요청("강남역");
        StationResponse 역삼역 = 지하철역_생성_요청("역삼역");
        // When
        List<StationResponse> 지하철역_리스트 = 지하철역_모두_조회();
        // Then
        지하철역_리스트_조회_완료(지하철역_리스트, Arrays.asList(강남역, 역삼역));
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // Given
        ExtractableResponse<Response> 강남역 = 지하철역_생성_요청후_응답("강남역");
        // When
        ExtractableResponse<Response> 삭제_요청_응답 = 지하철역_삭제_요청(강남역);
        // Then
        지하철역_삭제_성공(삭제_요청_응답);
    }

    private void 지하철_생성_완료(ExtractableResponse<Response> response) {
        컨텐츠_생성됨(response);
    }

    private void 지하철역_생성_실패(ExtractableResponse<Response> response) {
        잘못된_요청(response);
    }

    private void 지하철역_리스트_조회_완료(List<StationResponse> actual, List<StationResponse> expected) {
        assertEquals(actual, expected);
    }

    private void 지하철역_삭제_성공(ExtractableResponse<Response> response) {
        컨텐츠_없음(response);
    }
}
