package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.StationTestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다. 종점역(상행, 하행) 정보를 생성한다.")
    @Test
    void createLine() {
        //given
        StationTestHelper.지하철_역_생성_요청("건대역");
        StationTestHelper.지하철_역_생성_요청("용마산역");
        Map<String, String> params = LineMap.of("bg-red-600", "신분당선", "1", "2", "10");

        // when 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_생성_요청(params);

        // then 지하철_노선_생성됨
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value()),
                () -> assertThat(response.header("Location")).isNotBlank()
        );

    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        Map<String, String> params = LineMap.of("bg-red-600", "신분당선");
        LineTestHelper.지하철_노선_등록되어_있음(params);

        // when 지하철_노선_생성_요청
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_생성_요청(params);

        // then 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        // 지하철_노선_등록되어_있음
        StationTestHelper.지하철_역_생성_요청("건대역");
        StationTestHelper.지하철_역_생성_요청("용마산역");
        Map<String, String> params1 = LineMap.of("green darken-2", "7호선","1", "2", "10");
        ExtractableResponse<Response> createResponse1 = LineTestHelper.지하철_노선_생성_요청(params1);

        // 지하철_노선_등록되어_있음
        StationTestHelper.지하철_역_생성_요청("분당역");
        StationTestHelper.지하철_역_생성_요청("강남역");
        Map<String, String> params2 = LineMap.of("bg-red-600", "신분당선", "3", "4", "5");
        ExtractableResponse<Response> createResponse2 = LineTestHelper.지하철_노선_생성_요청(params2);

        // when 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_목록_조회_요청();

        // then
        LineTestHelper.지하철_노선_목록_조회_됨(createResponse1, createResponse2, response);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given 지하철_노선_등록되어_있음
        StationTestHelper.지하철_역_생성_요청("분당역");
        StationTestHelper.지하철_역_생성_요청("강남역");
        Map<String, String> params = LineMap.of("bg-red-600", "신분당선");
        LineTestHelper.지하철_노선_등록되어_있음(params);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_조회_요청();

        // then 지하철_노선_응답됨
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(response.body()).isNotNull()
        );
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given 지하철_노선_등록되어_있음
        Map<String, String> params = LineMap.of("bg-red-600", "신분당선");
        LineTestHelper.지하철_노선_등록되어_있음(params);

        // when 지하철_노선_수정_요청
        Map<String, String> updateParams = LineMap.of("bg-blue-600", "구분당선");
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_수정_요청(updateParams);

        // then 지하철_노선_수정됨
        LineResponse finalResponse = response.jsonPath().getObject(".", LineResponse.class);
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value()),
                () -> assertThat(finalResponse.getName()).isEqualTo("구분당선")
        );
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given 지하철_노선_등록되어_있음
        Map<String, String> params = LineMap.of("bg-red-600", "신분당선");
        LineTestHelper.지하철_노선_등록되어_있음(params);

        // when
        ExtractableResponse<Response> response = LineTestHelper.지하철_노선_제거_요청();

        // then // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
