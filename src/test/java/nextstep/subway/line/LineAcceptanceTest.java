package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nextstep.subway.line.LineAcceptanceTestMethod.*;
import static nextstep.subway.utils.Fixture.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));

        // then
        응답_확인_CREATED(response);
        지하철_노선_생성_확인(response, 신분당선);
    }


    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLineWithDuplicateName() {
        // given
        신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));

        // when
        ExtractableResponse<Response> actual = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(홍대역), 역_생성(신촌역));

        // then
        응답_확인_BAD_REQUEST(actual);
    }


    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void findAllLine() {
        LineResponse response1 = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역)).as(LineResponse.class);
        LineResponse response2 = 신규_지하철_노선_생성_요청("/lines", 이호선, 16, 역_생성(홍대역), 역_생성(신촌역)).as(LineResponse.class);

        // when
        ExtractableResponse<Response> actual = 지하철_노선_목록_조회_요청("/lines");

        // then
        응답_확인_OK(actual);
        지하철_노선_목록_확인(actual, Stream.concat(response1.getStations().stream(),
                response2.getStations().stream()).collect(Collectors.toList()));
    }


    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void findOneLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));

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
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));

        // when
        ExtractableResponse<Response> actual = 지하철_노선_수정_요청("/lines/{id}", 이호선, response.as(LineResponse.class).getId());

        // then
        응답_확인_OK(actual);
    }

    @DisplayName("지하철 노선을 수정한다.(없을 경우 오류 발생)")
    @Test
    void updateEmptyLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));

        // when
        ExtractableResponse<Response> actual = 지하철_노선_수정_요청("/lines/{id}", 이호선, response.as(LineResponse.class).getId() + 1);

        // then
        응답_확인_BAD_REQUEST(actual);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));

        // when
        ExtractableResponse<Response> actual = 지하철_노선_삭제_요청("/lines/{id}", response.as(LineResponse.class).getId());

        // then
        응답_확인_NO_CONTENT(actual);
    }

    @DisplayName("지하철 노선을 제거한다.(없을 경우 오류 발생)")
    @Test
    void deleteEmptyLine() {
        // given
        ExtractableResponse<Response> response = 신규_지하철_노선_생성_요청("/lines", 신분당선, 10, 역_생성(강남역), 역_생성(광교역));

        // when
        ExtractableResponse<Response> actual = 지하철_노선_삭제_요청("/lines/{id}", response.as(LineResponse.class).getId() + 1);

        // then
        응답_확인_BAD_REQUEST(actual);
    }

}
