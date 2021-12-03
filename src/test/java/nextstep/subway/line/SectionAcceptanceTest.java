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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static nextstep.subway.line.TestLineAcceptanceFactory.종점역정보_파라미터_생성;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_ID_추출;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_목록_응답됨;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_생성_실패됨;
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
    LineRequest 신분당선_강남역_양재시민의숲;
    LineRequest 신분당선_양재역_양재시민의숲;

    @BeforeEach
    public void setUp() {
        super.setUp();

        // given
        강남역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("강남역")));
        양재역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("양재역")));
        양재시민의숲 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("양재시민의숲")));
        청계산입구 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("청계산입구")));
        신분당선_강남역_양재역 = 지하철_노선과_종점역정보_파라미터_생성("신분당선", "red", 강남역.getId(), 양재역.getId(), 10);
        신분당선_강남역_양재시민의숲 = 지하철_노선과_종점역정보_파라미터_생성("신분당선", "red", 강남역.getId(), 양재시민의숲.getId(), 10);
        신분당선_양재역_양재시민의숲 = 지하철_노선과_종점역정보_파라미터_생성("신분당선", "red", 양재역.getId(), 양재시민의숲.getId(), 10);
    }

    @Test
    void 지하철_노선에_종점역_정보를_추가한다() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재역);
        long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 양재역_양재시민의숲 = 종점역정보_파라미터_생성(양재역.getId(), 양재시민의숲.getId(), 8);
        SectionRequest 양재시민의숲_청계산입구 = 종점역정보_파라미터_생성(양재시민의숲.getId(), 청계산입구.getId(), 8);
        생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 양재역_양재시민의숲);
        생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 양재시민의숲_청계산입구);

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_요청_응답 = 조회_요청(LINE_ROOT_PATH);

        // then
        지하철_노선_목록_응답됨(지하철_노선_목록_조회_요청_응답);
    }

    @Test
    @DisplayName("구간들 사이에 구간을 추가한다. 강남역 -(10m)- 양재시민의숲 => 강남역 -(8m)- 양재역 -(2m)- 양재시민의숲")
    void 구간들_사이에_새로운_구간을_추가한다() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재시민의숲);
        long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 강남역_양재역 = 종점역정보_파라미터_생성(강남역.getId(), 양재역.getId(), 8);
        생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 강남역_양재역);

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_요청_응답 = 조회_요청(LINE_ROOT_PATH);

        // then
        지하철_노선_목록_응답됨(지하철_노선_목록_조회_요청_응답);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 11})
    void 기존_구간의_사이_길이_보다_크거나_같으면_등록할_수_없다(int distance) {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재시민의숲);
        long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 강남역_양재역 = 종점역정보_파라미터_생성(강남역.getId(), 양재역.getId(), distance);

        // when
        ExtractableResponse<Response> 구간_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 강남역_양재역);

        // then
        지하철_노선_생성_실패됨(구간_생성_요청_응답);
    }

    @Test
    void 상행역과_하행역이_이미_노선에_등록되어있으면_추가할_수_없다() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재역);
        long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 강남역_양재역 = 종점역정보_파라미터_생성(강남역.getId(), 양재역.getId(), 8);

        // when
        ExtractableResponse<Response> 구간_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 강남역_양재역);

        // then
        지하철_노선_생성_실패됨(구간_생성_요청_응답);
    }

    @Test
    void 상행역과_하행역_둘다_노선에_등록되어있지_않으면_추가할_수_없다() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재역);
        long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 양재시민의숲_청계산입구 = 종점역정보_파라미터_생성(양재시민의숲.getId(), 청계산입구.getId(), 8);

        // when
        ExtractableResponse<Response> 구간_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 양재시민의숲_청계산입구);

        // then
        지하철_노선_생성_실패됨(구간_생성_요청_응답);
    }

    @Test
    @DisplayName("새로운 구간을 상행 종점으로 추가한다. 양재역 -(10m)- 양재시민의숲 => 강남역 -(10m)- 양재역 -(10m)- 양재시민의숲")
    void 새로운_구간을_상행_종점으로_추가한다() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_양재역_양재시민의숲);
        long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 강남역_양재역 = 종점역정보_파라미터_생성(강남역.getId(), 양재역.getId(), 10);
        생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 강남역_양재역);

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_요청_응답 = 조회_요청(LINE_ROOT_PATH);

        // then
        지하철_노선_목록_응답됨(지하철_노선_목록_조회_요청_응답);
    }

    @Test
    @DisplayName("새로운_구간을_하행_종점으로_추가한다. 강남역 -(10m)- 양재역 => 강남역 -(10m)- 양재역 -(10m)- 양재시민의숲")
    void 새로운_구간을_하행_종점으로_추가한다() {
        // given
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재역);
        long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        SectionRequest 강남역_양재시민의숲 = 종점역정보_파라미터_생성(양재역.getId(), 양재시민의숲.getId(), 10);
        생성_요청(LINE_ROOT_PATH + 신분당선_ID + LINE_ADD_SECTIONS_PATH, 강남역_양재시민의숲);

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_요청_응답 = 조회_요청(LINE_ROOT_PATH);

        // then
        지하철_노선_목록_응답됨(지하철_노선_목록_조회_요청_응답);
    }
}
