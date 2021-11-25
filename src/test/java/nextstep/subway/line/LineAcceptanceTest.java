package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineEditRequest;
import nextstep.subway.line.dto.LineRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static nextstep.subway.line.LineScenarioMethod.*;
import static nextstep.subway.station.StationScenarioMethod.등록되지_않은_지하철_종점역;
import static nextstep.subway.station.StationScenarioMethod.지하철_종점역_정보;
import static org.springframework.http.HttpStatus.*;

@DisplayName("지하철 노선 인수 테스트")
class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        Map<String, Long> terminus = 지하철_종점역_정보("강남", "광교");
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", terminus, 13);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("지하철 노선이 이미 등록되어 있는 경우 지하철 노선 생성에 실패한다.")
    @Test
    void createLineWithSameLine() {
        // given
        Map<String, Long> terminus = 지하철_종점역_정보("강남", "광교");
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", terminus, 13);
        지하철_노선_등록되어_있음(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(response, BAD_REQUEST.value());
    }

    @DisplayName("지하철 역이 등록되어 있지 않은 경우 지하철 노선 생성에 실패한다.")
    @Test
    void createLineWithoutTerminus() {
        // given
        Map<String, Long> terminus = 등록되지_않은_지하철_종점역();
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", terminus, 13);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선);

        // then
        지하철_노선_생성_실패됨(response, NOT_FOUND.value());
    }

    @DisplayName("지하철 구간이 등록되어 있지 않은 경우 지하철 노선 생성에 실패한다.")
    @Test
    void createLineWithoutSection() {
        // given

        // when

        // then
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findLines() {
        // given
        Map<String, Long> terminus = 지하철_종점역_정보("강남", "광교");
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", terminus, 13);
        LineRequest 수인선 = 지하철_노선_정보("수인선", "bg-yellow-600", terminus, 14);

        지하철_노선_등록되어_있음(신분당선);
        지하철_노선_등록되어_있음(수인선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청("/lines");

        // then
        지하철_노선_조회_응답됨(response);
        지하철_노선_목록_조회_결과_포함됨(response, 신분당선);
        지하철_노선_목록_조회_결과_포함됨(response, 수인선);
    }

    @DisplayName("지하철 노선 1건을 조회한다.")
    @Test
    void findLine() {
        // given
        Map<String, Long> terminus = 지하철_종점역_정보("오이도", "인천");
        LineRequest 수인선 = 지하철_노선_정보("수인선", "bg-yellow-600", terminus, 14);
        String createdLocationUri = 지하철_노선_등록되어_있음(수인선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(createdLocationUri);

        // then
        지하철_노선_조회_응답됨(response);
        지하철_노선_조회_결과_일치됨(response, 수인선);
        지하철_노선_구간_정렬됨(response, terminus);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 조회가 실패한다.")
    @Test
    void findLineValidateNotFound() {
        // given
        String notFoundUri = "lines/1";

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(notFoundUri);

        // then
        지하철_노선_조회_실패됨(response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        Map<String, Long> terminus = 지하철_종점역_정보("강남", "광교");
        LineRequest 신분당선 = 지하철_노선_정보("신분당선", "bg-red-600", terminus, 13);

        String createdLocationUri = 지하철_노선_등록되어_있음(신분당선);
        LineEditRequest 구분당선 = new LineEditRequest("구분당선", "bg-blue-600");

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(createdLocationUri, 구분당선);

        // then
        지하철_노선_수정됨(response);
        지하철_노선_수정_결과_일치됨(createdLocationUri, 구분당선);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 수정이 실패한다.")
    @Test
    void updateLineValidateNotFound() {
        // given

        // when

        // then
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        Map<String, Long> terminus = 지하철_종점역_정보("오이도", "인천");
        LineRequest 수인선 = 지하철_노선_정보("수인선", "bg-yellow-600", terminus, 14);
        String createdLocationUri = 지하철_노선_등록되어_있음(수인선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(createdLocationUri);

        // then
        지하철_노선_삭제됨(response);
    }

    @DisplayName("지하철 노선이 등록되지 않은 경우 지하철 노선 제거가 실패한다.")
    @Test
    void deleteLineValidateEmptyResult() {
        // given
        String notFoundUri = "lines/1";

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(notFoundUri);

        // then
        지하철_노선_삭제_실패됨(response);
    }
}
