package nextstep.subway.line;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.List;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 노선_생성() {
        // when
        ExtractableResponse<Response> response
            = LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "강남역", "역삼역", 10);

        // then
        // 지하철_노선_생성됨
        LineResponse lineResponse = response.as(LineResponse.class);

        노선생성시_상태헤더숫자생성이름_확인(response, lineResponse);
    }

    void 노선생성시_상태헤더숫자생성이름_확인(ExtractableResponse<Response> response, LineResponse lineResponse) {
        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.header(HttpHeaders.LOCATION)).isNotBlank();
        assertThat(lineResponse.getStations().size()).isEqualTo(2);
        assertThat(lineResponse.getStations().stream().map(Station::getName))
            .contains("강남역", "역삼역");
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void 중복_노선_생성() {
        // given
        // 지하철_노선_등록되어_있음
        LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "강남역", "역삼역", 10);

        // when
        ExtractableResponse<Response> response
            = LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "양재역", "판교역", 10);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void 노선_목록_조회() {
        // given
        ExtractableResponse<Response> createResponse1
            = LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "강남역", "역삼역", 10);
        ExtractableResponse<Response> createResponse2
            = LineAcceptanceTool.지하철_노선_생성_요청("4호선", "bg-blue-400"
            , "양재역", "판교역", 20);

        // when
        // 지하철_노선_목록_조회_요청
        ExtractableResponse<Response> response = LineAcceptanceTool.지하철_노선_목록_조회_요청();

        // then
        // 지하철_노선_목록_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        // 지하철_노선_목록_포함됨
        List<LineResponse> results = response.jsonPath().getList(".", LineResponse.class);
        assertThat(results).contains(createResponse1.as(LineResponse.class),
            createResponse2.as(LineResponse.class));

    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 노선_조회() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
            = LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "강남역", "역삼역", 10);

        // when
        // 지하철_노선_조회_요청
        ExtractableResponse<Response> response
            = LineAcceptanceTool.지하철_노선_조회_요청(createResponse1.header(HttpHeaders.LOCATION));

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        LineResponse createResponse = createResponse1.as(LineResponse.class);
        LineResponse findResponse = response.as(LineResponse.class);
        assertEquals(createResponse.getId(), findResponse.getId());
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void 노선_수정_테스트() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
            = LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "강남역", "역삼역", 10);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response
            = LineAcceptanceTool.지하철_노선_수정_요청("구분당선"
            , "bg-red-600"
            , createResponse1.header(HttpHeaders.LOCATION));
        LineResponse findUpdateLineResponse
            = LineAcceptanceTool.지하철_노선_조회_요청(createResponse1.header(HttpHeaders.LOCATION))
            .as(LineResponse.class);

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        assertThat(findUpdateLineResponse.getName()).isEqualTo("구분당선");
    }

    @DisplayName("ID가 없는 노선을 수정 요청 하는 경우 오류 발생")
    @Test
    void 없는_노선_수정_테스트() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
            = LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "강남역", "역삼역", 10);

        // when
        // 지하철_노선_수정_요청
        ExtractableResponse<Response> response
            = LineAcceptanceTool.지하철_노선_수정_요청("구분당선"
            , "bg-red-600"
            , createResponse1.header(HttpHeaders.LOCATION) + "00");

        // then
        // 지하철_노선_수정됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void 지하철_노선_제거() {
        // given
        // 지하철_노선_등록되어_있음
        ExtractableResponse<Response> createResponse1
            = LineAcceptanceTool.지하철_노선_생성_요청("신분당선", "bg-red-600"
            , "강남역", "역삼역", 10);

        // when
        // 지하철_노선_제거_요청
        ExtractableResponse<Response> response
            = LineAcceptanceTool.지하철_노선_제거_요청(createResponse1.header(HttpHeaders.LOCATION));

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }
}
