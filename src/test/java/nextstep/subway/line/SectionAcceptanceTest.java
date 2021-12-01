package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static nextstep.subway.line.TestLineAcceptanceFactory.종점역정보_파라미터_생성;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_ID_추출;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_목록_응답됨;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선과_종점역정보_파라미터_생성;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_생성;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_파라미터_생성;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    StationResponse 강남역;
    StationResponse 양재역;
    StationResponse 양재시민의숲;
    StationResponse 청계산입구;

    LineRequest 신분당선_강남역_양재역;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("강남역")));
        양재역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("양재역")));
        양재시민의숲 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("양재시민의숲")));
        청계산입구 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("청계산입구")));
        신분당선_강남역_양재역 = 지하철_노선과_종점역정보_파라미터_생성("신분당선", "red", 강남역.getId(), 양재역.getId(), 10);
    }

    @Test
    void 지하철_노선에_종점역_정보를_추가한다() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재역);
        Long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 양재역_양재시민의숲 = 종점역정보_파라미터_생성(양재역.getId(), 양재시민의숲.getId(), 8);
        SectionRequest 양재시민의숲_청계산입구 = 종점역정보_파라미터_생성(양재시민의숲.getId(), 청계산입구.getId(), 8);
        생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 양재역_양재시민의숲);
        생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 양재시민의숲_청계산입구);

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_요청_응답 = 조회_요청(LINE_ROOT_PATH);

        // then
        지하철_노선_목록_응답됨(지하철_노선_목록_조회_요청_응답);
    }
}
