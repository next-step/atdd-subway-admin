package nextstep.subway.section;

import static nextstep.subway.section.SectionAcceptanceStep.*;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.LineAcceptanceStep;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.station.StationAcceptanceStep;
import nextstep.subway.station.dto.StationRequest;

@DisplayName("지하철 구간 삭제 관련 기능")
public class SectionRemoveAcceptanceTest extends AcceptanceTest {

    @DisplayName("노선의 구간 내 중간 역을 제거한다.")
    @Test
    void removeStation() {
        // given
        Long 판교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("판교역"));
        Long 수지역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("수지역"));
        LineRequest lineRequest = LineRequest.of("신분당선", "bg-red-600", 판교역_ID, 수지역_ID, 4);
        Long 신분당선_ID = LineAcceptanceStep.지하철_노선_등록되어_있음(lineRequest);

        Long 광교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("광교역"));
        SectionRequest sectionRequest = SectionRequest.of(수지역_ID, 광교역_ID, 4);
        지하철_노선에_구간_등록_요청(신분당선_ID, sectionRequest);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선의_구간_역_제외_요청(신분당선_ID, 수지역_ID);

        // then
        지하철_노선의_구간_역_제외됨(removeResponse);

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_조회_요청(신분당선_ID);

        // then
        LineAcceptanceStep.지하철_노선_응답됨(response);
        지하철_노선에_역_순서_정렬됨(response, Arrays.asList(판교역_ID, 광교역_ID));
    }

    @DisplayName("노선의 구간 내 마지막 역을 제거한다.")
    @Test
    void removeEndStation() {
        // given
        Long 판교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("판교역"));
        Long 수지역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("수지역"));
        LineRequest lineRequest = LineRequest.of("신분당선", "bg-red-600", 판교역_ID, 수지역_ID, 4);
        Long 신분당선_ID = LineAcceptanceStep.지하철_노선_등록되어_있음(lineRequest);

        Long 광교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("광교역"));
        SectionRequest sectionRequest = SectionRequest.of(수지역_ID, 광교역_ID, 4);
        지하철_노선에_구간_등록_요청(신분당선_ID, sectionRequest);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선의_구간_역_제외_요청(신분당선_ID, 광교역_ID);

        // then
        지하철_노선의_구간_역_제외됨(removeResponse);

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_조회_요청(신분당선_ID);

        // then
        LineAcceptanceStep.지하철_노선_응답됨(response);
        지하철_노선에_역_순서_정렬됨(response, Arrays.asList(판교역_ID, 수지역_ID));
    }

    @DisplayName("노선의 구간 내 첫 역을 제거한다.")
    @Test
    void removeStartStation() {
        // given
        Long 판교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("판교역"));
        Long 수지역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("수지역"));
        LineRequest lineRequest = LineRequest.of("신분당선", "bg-red-600", 판교역_ID, 수지역_ID, 4);
        Long 신분당선_ID = LineAcceptanceStep.지하철_노선_등록되어_있음(lineRequest);

        Long 광교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("광교역"));
        SectionRequest sectionRequest = SectionRequest.of(수지역_ID, 광교역_ID, 4);
        지하철_노선에_구간_등록_요청(신분당선_ID, sectionRequest);

        // when
        ExtractableResponse<Response> removeResponse = 지하철_노선의_구간_역_제외_요청(신분당선_ID, 판교역_ID);

        // then
        지하철_노선의_구간_역_제외됨(removeResponse);

        // when
        ExtractableResponse<Response> response = LineAcceptanceStep.지하철_노선_조회_요청(신분당선_ID);

        // then
        LineAcceptanceStep.지하철_노선_응답됨(response);
        지하철_노선에_역_순서_정렬됨(response, Arrays.asList(수지역_ID, 광교역_ID));
    }

    @DisplayName("노선의 구간 내 등록되지 않는 역을 제한다.")
    @Test
    void notRegisterRemoveStation() {
        // given
        Long 판교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("판교역"));
        Long 수지역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("수지역"));
        LineRequest lineRequest = LineRequest.of("신분당선", "bg-red-600", 판교역_ID, 수지역_ID, 4);
        Long 신분당선_ID = LineAcceptanceStep.지하철_노선_등록되어_있음(lineRequest);

        Long 광교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("광교역"));
        SectionRequest sectionRequest = SectionRequest.of(수지역_ID, 광교역_ID, 4);
        지하철_노선에_구간_등록_요청(신분당선_ID, sectionRequest);

        // given
        Long 강남역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("강남역"));
        // when
        // 지하철_노선의_구간_역_제외_요청
        ExtractableResponse<Response> removeResponse = 지하철_노선의_구간_역_제외_요청(신분당선_ID, 강남역_ID);

        // then
        지하철_노선의_구간_역_제외_실패됨(removeResponse);
    }

    @DisplayName("노선의 구간이 하나인 경우 역을 제한다.")
    @Test
    void removeStationInOneSection() {
        // given
        Long 판교역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("판교역"));
        Long 수지역_ID = StationAcceptanceStep.지하철_역_등록되어_있음(new StationRequest("수지역"));
        LineRequest lineRequest = LineRequest.of("신분당선", "bg-red-600", 판교역_ID, 수지역_ID, 4);
        Long 신분당선_ID = LineAcceptanceStep.지하철_노선_등록되어_있음(lineRequest);

        // when
        // 지하철_노선의_구간_역_제외_요청
        ExtractableResponse<Response> removeResponse = 지하철_노선의_구간_역_제외_요청(신분당선_ID, 판교역_ID);

        // then
        지하철_노선의_구간_역_제외_실패됨(removeResponse);
    }
}
