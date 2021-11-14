package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.line.LineStep.*;
import static nextstep.subway.station.StationStep.지하철역_생성되어_있음;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {

    private static LineRequest 신분당선_요청;
    private static LineRequest 이호선_요청;
    private static LineRequest 구분당선_요청 = new LineRequest("구분당선", "bg-blue-600");

    private static StationRequest 강남역_요청 = new StationRequest("강남역");
    private static StationRequest 역삼역_요청 = new StationRequest("역삼역");
    private static StationRequest 교대역_요청 = new StationRequest("교대역");

    private StationResponse 강남역;
    private StationResponse 역삼역;
    private StationResponse 교대역;

    @BeforeEach
    void beforeEach() {
        super.setUp();

        강남역 = 지하철역_생성되어_있음(강남역_요청);
        역삼역 = 지하철역_생성되어_있음(역삼역_요청);
        교대역 = 지하철역_생성되어_있음(교대역_요청);

        신분당선_요청 = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 역삼역.getId(), 10);
        이호선_요청 = new LineRequest("2호선", "bg-green-600", 강남역.getId(), 교대역.getId(), 30);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_요청);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_등록되어_있음(신분당선_요청);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(신분당선_요청);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선_요청);
        LineResponse 이호선_응답 = 지하철_노선_등록되어_있음(이호선_요청);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(신분당선_응답, 이호선_응답));
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void getLine() {
        // given
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선_요청);

        // when
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(신분당선_응답);

        // then
        지하철_노선_응답됨(신분당선_응답, response);
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선_요청);

        // when
        ExtractableResponse<Response> response = 지하철_노선_수정_요청(신분당선_응답, 구분당선_요청);

        // then
        지하철_노선_수정됨(response);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        LineResponse 신분당선_응답 = 지하철_노선_등록되어_있음(신분당선_요청);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(신분당선_응답);

        // then
        지하철_노선_삭제됨(response);
    }
}
