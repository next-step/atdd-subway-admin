package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Map;

import static nextstep.subway.line.LineAcceptanceTestUtils.*;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성_요청_및_성공_체크;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
class LineAcceptanceTest extends AcceptanceTest {

    private static final String URL_BASE = "/lines";
    private static final String URL_GET_LINES = URL_BASE;
    private static final String URL_CREATE_LINE = URL_BASE;
    private static final String URL_GET_LINE = URL_BASE + "/%d";
    private static final String URL_UPDATE_LINE = URL_BASE + "/%d";
    private static final String URL_DELETE_LINE = URL_BASE + "/%d";

    private StationResponse 강남역;
    private StationResponse 역삼역;

    @BeforeEach
    void setUpStations() {
        // given
        // 지하철 역 생성 요청
        // 지하철 역 생성 요청
        강남역 = 지하철역_생성_요청_및_성공_체크("강남역");
        역삼역 = 지하철역_생성_요청_및_성공_체크("역삼역");
    }


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        // 지하철_노선_생성_요청
        Map<String, Object> params = makeParams("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId() ,10);
        ExtractableResponse<Response> line_신분당선 = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성됨
        assertThat(line_신분당선.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicate() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, Object> params = makeParams("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId() ,10);
        지하철_노선_생성_요청_및_성공_체크(params);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> line_신분당선 = 지하철_노선_생성_요청(params);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(line_신분당선.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        // 지하철_노선_등록되어_있음
        LineResponse line_신분당선 = 지하철_노선_생성_요청_및_성공_체크(makeParams("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId() ,10));
        LineResponse line_2호선 = 지하철_노선_생성_요청_및_성공_체크(makeParams("2호선", "bg-green-600", 강남역.getId(), 역삼역.getId() ,10));

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> lines = 지하철_노선_목록_조회_요청();
        
        // then
        // 지하철_노선_목록_응답됨
        // 지하철_노선_목록_포함됨
        assertThat(lines.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(containsToResponse(Arrays.asList(line_신분당선, line_2호선), convertToLineResponses(lines))).isTrue();
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        // 지하철_노선_등록되어_있음
        LineResponse line_신분당선 = 지하철_노선_생성_요청_및_성공_체크(makeParams("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId() ,10));

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> line = 지하철_노선_조회_요청(line_신분당선.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(line.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse actual = line.as(LineResponse.class);
        assertAll(() -> {
            assertThat(actual.getName()).isEqualTo("신분당선");
            assertThat(actual.getColor()).isEqualTo("bg-red-600");
        });
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, Object> params = makeParams("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId() ,10);
        LineResponse line_신분당선 = 지하철_노선_생성_요청_및_성공_체크(params);

        // when
        // 지하철_노선_수정_요청
        params = makeParams("구분당선", "bg-blue-600", 강남역.getId(), 역삼역.getId() ,10);
        ExtractableResponse<Response> line_구분당선 = 지하철_노선_수정_요청(line_신분당선.getId(), params);

        // then
        // 지하철_노선_수정됨
        assertThat(line_구분당선.statusCode()).isEqualTo(HttpStatus.OK.value());;
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        // 지하철_노선_등록되어_있음
        Map<String, Object> params = makeParams("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId() ,10);
        LineResponse line_신분당선 = 지하철_노선_생성_요청_및_성공_체크(params);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response = 지하철_노선_삭제_요청(line_신분당선.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }


    public static ExtractableResponse<Response> 지하철_노선_생성_요청(final Map<String, Object> params) {
        // when
        return post(URL_CREATE_LINE, params);
    }

    public static LineResponse 지하철_노선_생성_요청_및_성공_체크(final Map<String, Object> params) {
        ExtractableResponse<Response> line = 지하철_노선_생성_요청(params);
        assertThat(line.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        return line.as(LineResponse.class);
    }

    public static ExtractableResponse<Response> 지하철_노선_목록_조회_요청() {
        // when
        return get(URL_GET_LINES);
    }

    public static ExtractableResponse<Response> 지하철_노선_조회_요청(final Long id) {
        // when
        return get(String.format(URL_GET_LINE, id));
    }

    public static ExtractableResponse<Response> 지하철_노선_수정_요청(final Long id, final Map<String, Object> params) {
        // when
        return put(String.format(URL_UPDATE_LINE, id), params);
    }

    public static ExtractableResponse<Response> 지하철_노선_삭제_요청(final Long id) {
        // when
        return delete(String.format(URL_DELETE_LINE, id));
    }
}
