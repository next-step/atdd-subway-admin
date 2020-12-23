package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static nextstep.subway.station.step.StationAcceptanceStepTest.*;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_성공됨(response);
        지하철역_생성_응답됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_생성_요청("강남역");

        // then
        지하철역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        List<ExtractableResponse<Response>> responses = 지하철역_등록되어_있음(Arrays.asList("강남역", "역삼역"));

        // when
        ExtractableResponse<Response> response = 지하철역_모든_리스트_요청();

        // then
        지하철역_생성_성공됨(response);
        지하철역_생성된_리스트_응답됨(responses, response);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        ExtractableResponse<Response> createResponse = 지하철역_생성_요청("강남역");

        // when
        ExtractableResponse<Response> response = 지하철역_삭제_요청(createResponse.header("Location"));

        // then
        지하철역_삭제_성공됨(response);
    }

}
