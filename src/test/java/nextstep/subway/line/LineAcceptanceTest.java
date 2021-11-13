package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static nextstep.subway.line.LineAcceptanceTestMethod.*;
import static nextstep.subway.utils.HttpUtils.post;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600");
    private static final LineRequest 이호선 = new LineRequest("2호선", "green");

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // given
        LineRequest request = 신분당선;

        // when
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", request);

        // then
        응답_확인_CREATED(response);
        지하철_노선_생성_확인(response);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        신규_지하철_노선_생성_요청("/lines", 신분당선);

        // when
        ExtractableResponse<Response> actual = 신규_지하철_노선_생성_요청("/lines", 신분당선);

        // then
        응답_확인_BAD_REQUEST(actual);
        지하철_노선_생성_오류_확인(actual);
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllLine() {
        // given
        List<Long> excepted = new ArrayList<>();
        excepted.add(신규_지하철_노선_생성_요청("/lines", 신분당선).as(LineResponse.class).getId());
        excepted.add(신규_지하철_노선_생성_요청("/lines", 이호선).as(LineResponse.class).getId());

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
        ExtractableResponse<Response> 생성_신분당선 = 신규_지하철_노선_생성_요청("/lines", 신분당선);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_단건_조회("/lines/{id}", 생성_신분당선.as(LineResponse.class).getId());

        // then
        응답_확인_OK(actual);
        지하철_노선_단건_조회_확인(actual, 생성_신분당선.as(LineResponse.class).getId());
    }


    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 생성_신분당선 = 신규_지하철_노선_생성_요청("/lines", 신분당선);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_수정_요청("/lines/{id}", 이호선, 생성_신분당선.as(LineResponse.class).getId());

        // then
        응답_확인_OK(actual);
    }

    @DisplayName("지하철 노선을 수정한다.(없을 경우 오류 발생)")
    @Test
    void updateEmptyLine() {
        // given
        ExtractableResponse<Response> 생성_신분당선 = 신규_지하철_노선_생성_요청("/lines", 신분당선);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_수정_요청("/lines/{id}", 이호선, 생성_신분당선.as(LineResponse.class).getId() + 1);

        // then
        응답_확인_BAD_REQUEST(actual);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> 생성_신분당선 = 신규_지하철_노선_생성_요청("/lines", 신분당선);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_삭제_요청("/lines/{id}", 생성_신분당선.as(LineResponse.class).getId());

        // then
        응답_확인_NO_CONTENT(actual);
    }

    @DisplayName("지하철 노선을 제거한다.(없을 경우 오류 발생)")
    @Test
    void deleteEmptyLine() {
        // given
        ExtractableResponse<Response> 생성_신분당선 = 신규_지하철_노선_생성_요청("/lines", 신분당선);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_삭제_요청("/lines/{id}", 생성_신분당선.as(LineResponse.class).getId() + 1);

        // then
        응답_확인_BAD_REQUEST(actual);
    }

    private static final StationRequest 강남역  = new StationRequest("강남역");
    private static final StationRequest 광교역  = new StationRequest("광교역");

    @DisplayName("지하철 노선을 생성한다.(구간 정보 추가)")
    @Test
    void createLineAndSections() {
        // given
        Long 강남역_아이디 = 신규_역_생성_요청("/stations", 강남역).as(StationResponse.class).getId();
        Long 광교역_아이디 = 신규_역_생성_요청("/stations", 강남역).as(StationResponse.class).getId();
        신분당선.setUpStationId(강남역_아이디);
        신분당선.setDownStationId(광교역_아이디);

        LineRequest request = 신분당선;

        // when
        ExtractableResponse<Response> actual = 신규_지하철_노선_생성_요청("/lines", 신분당선);

        // then
        응답_확인_CREATED(actual);
        지하철_노선_생성_확인_종점_종점간거리_추가(actual, request);
    }

    private ExtractableResponse<Response> 신규_역_생성_요청(String path, StationRequest request) {
        return post(path, request);
    }

    private void 지하철_노선_생성_확인_종점_종점간거리_추가(ExtractableResponse<Response> actualResponse, LineRequest excepted) {
        LineResponse actual = actualResponse.as(LineResponse.class);
        assertAll(
                () -> assertThat(actual.getName()).isEqualTo(excepted.getName()),
                () -> assertThat(actual.getColor()).isEqualTo(excepted.getColor())
        );

    }

}
