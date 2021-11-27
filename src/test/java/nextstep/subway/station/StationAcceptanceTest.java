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
    @Test
    void 지하철역을_생성한다() {
        // given
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(params);

        // then
        지하철_역_생성됨(response);
    }

    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        지하철_역_등록되어_있음("강남역");
        Map<String, String> params = new HashMap<>();
        params.put("name", "강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_생성_요청(params);

        // then
        지하철_역_생성_실패됨(response);
    }

    @Test
    void 지하철역을_조회한다() {
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

    @Test
    void 지하철역을_제거한다() {
        // given
        Station station1 = 지하철_역_등록되어_있음("강남역");

        // when
        ExtractableResponse<Response> response = 지하철_역_제거_요청(station1.getId());

        // then
        지하철_역_삭제됨(response);
    }
}
