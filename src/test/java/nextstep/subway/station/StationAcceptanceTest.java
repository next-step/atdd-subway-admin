package nextstep.subway.station;

import static nextstep.subway.station.StationAcceptanceTestUtils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.domain.Station;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {
    @DisplayName("지하철역을 생성한다.")
    @Test
    void createStation() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(params);

        // then
        지하철_역_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철역 이름으로 지하철역을 생성한다.")
    @Test
    void createStationWithDuplicateName() {
        // given
        지하철_역_등록되어_있음("강남역");
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(params);

        // then
        지하철_역_생성_실패됨(response);
    }

    @DisplayName("지하철역을 조회한다.")
    @Test
    void getStations() {
        /// given
        Station station1 = 지하철_역_등록되어_있음("강남역");
        Station station2 = 지하철_역_등록되어_있음("역삼역");
        List<Long> expectedStationIds = Arrays.asList(station1.getId(), station2.getId());

        // when
        ExtractableResponse<Response> response = 지하철_역_목록_조회_요청();

        // then
        지하철_역_목록_생성됨(response);
        지하철_역_목록_포함됨(response, expectedStationIds);
    }

    @DisplayName("지하철역을 제거한다.")
    @Test
    void deleteStation() {
        // given
        Station station1 = 지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(station1.getId());

        // then
        지하철_역_삭제됨(response);
    }
}
