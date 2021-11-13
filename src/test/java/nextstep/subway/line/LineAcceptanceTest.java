package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.section.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTestMethod.*;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600");
    private static final LineRequest 이호선 = new LineRequest("2호선", "green");
    private static final StationRequest 강남역 = new StationRequest("강남역");
    private static final StationRequest 광교역 = new StationRequest("광교역");
    private static final StationRequest 홍대역 = new StationRequest("홍대역");
    private static final StationRequest 신촌역 = new StationRequest("신촌역");


    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역);

        // then
        응답_확인_CREATED(response);
        지하철_노선_생성_확인(response, 신분당선);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역);

        // when
        ExtractableResponse<Response> actual = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 홍대역, 신촌역);

        // then
        응답_확인_BAD_REQUEST(actual);
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllLine() {
        // given
        List<Long> excepted = new ArrayList<>();
        excepted.add(신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역).as(LineResponse.class).getId());
        excepted.add(신규_지하철_노선_생성_요청("/lines", 이호선, 10, 홍대역, 신촌역).as(LineResponse.class).getId());

        // when
        ExtractableResponse<Response> actual = 지하철_노선_목록_조회_요청("/lines");

        // then
        응답_확인_OK(actual);
        지하철_노선_목록_확인(actual, excepted);
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findOneLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_단건_조회("/lines/{id}", response.as(LineResponse.class).getId());

        // then
        응답_확인_OK(actual);
        지하철_노선_단건_조회_확인(actual, response.as(LineResponse.class).getId());
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_수정_요청("/lines/{id}", 이호선, response.as(LineResponse.class).getId());

        // then
        응답_확인_OK(actual);
    }

    @DisplayName("지하철 노선을 수정한다.(없을 경우 오류 발생)")
    @Test
    void updateEmptyLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_수정_요청("/lines/{id}", 이호선, response.as(LineResponse.class).getId() + 1);

        // then
        응답_확인_BAD_REQUEST(actual);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_삭제_요청("/lines/{id}", response.as(LineResponse.class).getId());

        // then
        응답_확인_NO_CONTENT(actual);
    }

    @DisplayName("지하철 노선을 제거한다.(없을 경우 오류 발생)")
    @Test
    void deleteEmptyLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_삭제_요청("/lines/{id}", response.as(LineResponse.class).getId() + 1);

        // then
        응답_확인_BAD_REQUEST(actual);
    }


    @DisplayName("지하철 노선 목록을 조회한다.(구간포함)")
    @Test
    void findAllLineAndSections() {
        // given
        List<Long> excepted = new ArrayList<>();
        excepted.add(신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 강남역, 광교역).as(LineResponse.class).getId());
        excepted.add(신규_지하철_노선_생성_요청("/lines", 이호선, 16, 홍대역, 신촌역).as(LineResponse.class).getId());

        // when
        ExtractableResponse<Response> actual = 지하철_노선_목록_조회_요청("/lines");

        // then
        응답_확인_OK(actual);
        지하철_노선_목록_확인_구간포함(actual);
    }

    public static void 지하철_노선_목록_확인_구간포함(ExtractableResponse<Response> actual) {
        List<LineResponse> result = actual.jsonPath().getList(".", LineResponse.class);
        assertThat(result).isNotEmpty();
    }

}
