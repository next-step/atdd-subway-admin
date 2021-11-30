package nextstep.subway.station;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.station.dto.StationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_ID_추출;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_IDs_추출;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_목록_IDs_추출;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_목록_응답됨;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_목록_포함됨;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_삭제됨;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_생성_실패됨;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_생성됨;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_파라미터_생성;

@DisplayName("지하철역 관련 기능")
public class StationAcceptanceTest extends AcceptanceTest {

    @Test
    void 지하철_역을_생성한다() {
        // when
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        ExtractableResponse<Response> 지하철_역_생성_요청_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);

        // then
        지하철_역_생성됨(지하철_역_생성_요청_응답);
    }

    @Test
    void 기존에_존재하는_지하철역_이름으로_지하철역을_생성한다() {
        // given
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        생성_요청(STATION_ROOT_PATH, 강남역);

        // when
        ExtractableResponse<Response> 지하철_역_생성_요청_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);

        // then
        지하철_역_생성_실패됨(지하철_역_생성_요청_응답);
    }

    @Test
    void 지하철역_목록을_조회한다() {
        /// given
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        StationRequest 역삼역 = 지하철_역_파라미터_생성("역삼역");
        ExtractableResponse<Response> 강남역_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);
        ExtractableResponse<Response> 역삼역_응답 = 생성_요청(STATION_ROOT_PATH, 역삼역);

        // when
        ExtractableResponse<Response> 지하철_역_목록_조회_요청_응답 = 조회_요청(STATION_ROOT_PATH);

        // then
        지하철_역_목록_응답됨(지하철_역_목록_조회_요청_응답);
        List<Long> expectedLineIds = 지하철_역_IDs_추출(강남역_응답, 역삼역_응답);
        List<Long> resultLineIds = 지하철_역_목록_IDs_추출(지하철_역_목록_조회_요청_응답);
        지하철_역_목록_포함됨(expectedLineIds, resultLineIds);
    }

    @Test
    void 지하철역을_제거한다() {
        // given
        StationRequest 강남역 = 지하철_역_파라미터_생성("강남역");
        ExtractableResponse<Response> 지하철_역_생성_요청_응답 = 생성_요청(STATION_ROOT_PATH, 강남역);
        Long 강남역_ID = 지하철_역_ID_추출(지하철_역_생성_요청_응답);

        // when
        ExtractableResponse<Response> 지하철_역_삭제_요청_응답 = 삭제_요청(STATION_ROOT_PATH + 강남역_ID);

        // then
        지하철_역_삭제됨(지하철_역_삭제_요청_응답);
    }
}
