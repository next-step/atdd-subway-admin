package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static nextstep.subway.line.TestLineAcceptanceFactory.종점역정보_파라미터_생성;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_ID_추출;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_IDs_추출;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_목록_IDs_추출;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_목록_응답됨;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_목록_포함됨;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_삭제됨;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_생성_실패됨;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_생성됨;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선_파라미터_생성;
import static nextstep.subway.line.TestLineAcceptanceFactory.지하철_노선과_종점역정보_파라미터_생성;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_생성;
import static nextstep.subway.station.TestStationAcceptanceFactory.지하철_역_파라미터_생성;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @Test
    void 지하철_노선을_생성한다() {
        // given
        LineRequest 신분당선 = 지하철_노선_파라미터_생성("신분당선", "red");

        // when
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선);

        // then
        지하철_노선_생성됨(지하철_노선_생성_요청_응답);
    }

    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        LineRequest 신분당선 = 지하철_노선_파라미터_생성("신분당선", "red");
        생성_요청(LINE_ROOT_PATH, 신분당선);

        // when
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선);

        // then
        지하철_노선_생성_실패됨(지하철_노선_생성_요청_응답);
    }

    @Test
    void 지하철_노선_목록을_조회한다() {
        // given
        LineRequest 신분당선 = 지하철_노선_파라미터_생성("신분당선", "red");
        LineRequest 이호선 = 지하철_노선_파라미터_생성("2호선", "green");
        ExtractableResponse<Response> 신분당선_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선);
        ExtractableResponse<Response> 이호선_응답 = 생성_요청(LINE_ROOT_PATH, 이호선);

        // when
        ExtractableResponse<Response> 지하철_노선_목록_조회_요청_응답 = 조회_요청(LINE_ROOT_PATH);

        // then
        지하철_노선_목록_응답됨(지하철_노선_목록_조회_요청_응답);
        List<Long> expectedLineIds = 지하철_노선_IDs_추출(신분당선_응답, 이호선_응답);
        List<Long> resultLineIds = 지하철_노선_목록_IDs_추출(지하철_노선_목록_조회_요청_응답);
        지하철_노선_목록_포함됨(expectedLineIds, resultLineIds);
    }

    @Test
    void 지하철_노선을_조회한다() {
        // given
        LineRequest 신분당선 = 지하철_노선_파라미터_생성("신분당선", "red");
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선);
        Long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        // when
        ExtractableResponse<Response> 지하철_노선_조회_요청_응답 = 조회_요청(LINE_ROOT_PATH + 신분당선_ID);

        // then
        지하철_노선_목록_응답됨(지하철_노선_조회_요청_응답);
    }

    @Test
    void 지하철_노선을_수정한다() {
        // given
        LineRequest 신분당선 = 지하철_노선_파라미터_생성("신분당선", "red");
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선);
        Long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        LineRequest 이호선 = 지하철_노선_파라미터_생성("2호선", "green");

        // when
        ExtractableResponse<Response> 지하철_노선_수정_요청_응답 = 수정_요청(LINE_ROOT_PATH + 신분당선_ID, 이호선);

        // then
        지하철_노선_목록_응답됨(지하철_노선_수정_요청_응답);
    }

    @Test
    void 지하철_노선을_제거한다() {
        // given
        LineRequest 신분당선 = 지하철_노선_파라미터_생성("신분당선", "red");
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선);
        Long 신분당선_ID = 지하철_노선_ID_추출(지하철_노선_생성_요청_응답);

        // when
        ExtractableResponse<Response> 지하철_노선_제거_요청_응답 = 삭제_요청(LINE_ROOT_PATH + 신분당선_ID);

        // then
        지하철_노선_삭제됨(지하철_노선_제거_요청_응답);
    }

    @Test
    void 지하철_노선을_종점역_정보와_함께_생성한다() {
        // given
        StationResponse 강남역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("강남역")));
        StationResponse 양재역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("양재역")));
        LineRequest 신분당선_강남역_양재역 = 지하철_노선과_종점역정보_파라미터_생성("신분당선", "red", 강남역.getId(), 양재역.getId(), 10);

        // when
        ExtractableResponse<Response> 지하철_노선_생성_요청_응답 = 생성_요청(LINE_ROOT_PATH, 신분당선_강남역_양재역);

        // then
        지하철_노선_생성됨(지하철_노선_생성_요청_응답);
    }

    @Test
    void 지하철_노선을_종점역_정보와_함께_조회한다() {
        // given
        StationResponse 강남역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("강남역")));
        StationResponse 양재역 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("양재역")));
        StationResponse 양재시민의숲 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("양재시민의숲")));
        StationResponse 청계산입구 = 지하철_역_생성(생성_요청(STATION_ROOT_PATH, 지하철_역_파라미터_생성("청계산입구")));

        LineRequest 신분당선_강남역_양재역 = 지하철_노선과_종점역정보_파라미터_생성("신분당선", "red", 강남역.getId(), 양재역.getId(), 10);
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
