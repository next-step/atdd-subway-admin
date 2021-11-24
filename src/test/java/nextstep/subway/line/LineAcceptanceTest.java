package nextstep.subway.line;

import static nextstep.subway.fixtrue.TestFactory.*;
import static nextstep.subway.station.StationAcceptanceTest.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.fixtrue.TestFactory;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    public static final String LINE_ONE = "1호선";
    public static final String LINE_ONE_COLOR_RED = "bg-red-600";
    public static final String LINE_TWO = "2호선";
    public static final String LINE_TWO_COLOR_GREEN = "bg-green-600";
    public static final String LINE_THREE = "3호선";
    public static final String LINE_THREE_YELLOW = "bg-yellow-600";
    public static final String ID = "id";
    public static final String 신촌역 = "신촌역";
    public static final String 서울역 = "서울역";
    public static final String 용산역 = "용산역";
    public static final String 강남역 = "강남역";

    @Test
    void 지하철_노선을_생성한다() {

        StationResponse station1 = 지하철역_등록되어_있음(new StationRequest(신촌역));
        StationResponse station2 = 지하철역_등록되어_있음(new StationRequest(강남역));
        LineRequest lineRequest = new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10);

        // when
        // 지하철_노선_생성_요청
        ExtractableResponse<Response> post = 지하철_노선_생성_요청(lineRequest);

        // then
        // 지하철_노선_생성됨
        assertThat(post.statusCode()).isEqualTo(CREATED.value());
    }

    @Test
    void 기존에_존재하는_지하철_노선_이름으로_지하철_노선을_생성한다() {
        // given
        // 지하철_노선_등록되어_있음
        StationResponse station1 = 지하철역_등록되어_있음(new StationRequest(신촌역));
        StationResponse station2 = 지하철역_등록되어_있음(new StationRequest(강남역));
        LineRequest createLine = new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10);
        지하철_노선_등록되어_있음(createLine);

        // when
        // 존재하는 지하철 노선 생성 요청
        ExtractableResponse<Response> post = 지하철_노선_생성_요청(createLine);

        // then
        // 지하철_노선_생성_실패됨
        assertThat(post.statusCode()).isEqualTo(BAD_REQUEST.value());
    }

    @Test
    void 지하철_노선_목록을_조회한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(new StationRequest(신촌역));
        StationResponse station2 = 지하철역_등록되어_있음(new StationRequest(강남역));
        StationResponse station3 = 지하철역_등록되어_있음(new StationRequest(서울역));
        StationResponse station4 = 지하철역_등록되어_있음(new StationRequest(용산역));
        지하철_노선_등록되어_있음(new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));
        지하철_노선_등록되어_있음(new LineRequest(LINE_TWO, LINE_TWO_COLOR_GREEN, station3.getId(), station4.getId(), 10));

        // when
        ExtractableResponse<Response> findResponse = 지하철_노선_조회_요청();

        // then
        assertThat(findResponse.statusCode()).isEqualTo(OK.value());
        지하철_응답_내용_확인(findResponse);
    }

    private void 지하철_응답_내용_확인(ExtractableResponse<Response> findResponse) {
        JsonPath jsonPath = findResponse.jsonPath();
        String lineName1 = jsonPath.getString("[0].name");
        String subwayName1 = jsonPath.getString("[0].stations[0].name");
        String lineName2 = jsonPath.getString("[1].name");
        String subwayName2 = jsonPath.getString("[1].stations[0].name");
        assertThat(lineName1).isEqualTo(LINE_ONE);
        assertThat(subwayName1).isEqualTo(신촌역);
        assertThat(lineName2).isEqualTo(LINE_TWO);
        assertThat(subwayName2).isEqualTo(서울역);
    }

    @Test
    void 지하철_노선을_조회한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(new StationRequest(신촌역));
        StationResponse station2 = 지하철역_등록되어_있음(new StationRequest(강남역));
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(lineResponse.getId());

        // then
        // 지하철_노선_응답됨
        assertThat(response.statusCode()).isEqualTo(OK.value());
    }

    @Test
    void 지하철_노선을_수정한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(new StationRequest(서울역));
        StationResponse station2 = 지하철역_등록되어_있음(new StationRequest(용산역));
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));

        // when
        LineRequest updateRequest = new LineRequest(LINE_THREE, LINE_THREE_YELLOW, station1.getId(), station2.getId(), 10);
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(lineResponse.getId(), updateRequest);

        // then
        LineResponse expectedResponse = toResponseData(response, LineResponse.class);
        assertAll(
            () -> assertThat(expectedResponse.getName().equals(LINE_THREE)).isTrue(),
            () -> assertThat(expectedResponse.getColor().equals(LINE_THREE_YELLOW)).isTrue()
        );
    }

    @Test
    void 지하철_노선을_제거한다() {

        // given
        StationResponse station1 = 지하철역_등록되어_있음(new StationRequest(서울역));
        StationResponse station2 = 지하철역_등록되어_있음(new StationRequest(용산역));
        LineResponse lineResponse = 지하철_노선_등록되어_있음(
            new LineRequest(LINE_ONE, LINE_ONE_COLOR_RED, station1.getId(), station2.getId(), 10));

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(lineResponse.getId());

        // then
        // 지하철_노선_삭제됨
        assertThat(response.statusCode()).isEqualTo(NO_CONTENT.value());
    }

    private ExtractableResponse<Response> 지하철_노선_생성_요청(Object obj) {
        return TestFactory.post("/lines", obj);
    }

    private LineResponse 지하철_노선_등록되어_있음(Object obj) {
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(obj);
        return response.jsonPath().getObject(".", LineResponse.class);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청(Long id) {
        return TestFactory.get("lines/{id}", id);
    }

    private ExtractableResponse<Response> 지하철_노선_조회_요청() {
        return TestFactory.get("lines");
    }

    private ExtractableResponse<Response> 지하철_노선_수정_요청(Long id, Object obj) {
        return TestFactory.update("lines/{id}", id, obj);
    }

    private ExtractableResponse<Response> 지하철_노선_제거_요청(Long id) {
        return TestFactory.delete("lines/{id}", id);
    }

}
