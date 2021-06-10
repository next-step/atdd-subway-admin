package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static nextstep.subway.line.LineSteps.*;
import static nextstep.subway.station.StationSteps.*;

@DisplayName("지하철 노선 관련 기능")
public class LineAcceptanceTest extends AcceptanceTest {
    private StationResponse 모란역;
    private StationResponse 복정역;
    private StationResponse 강남역;
    private StationResponse 광교역;
    private LineRequest 분당선;
    private LineRequest 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();
        모란역 = 지하철역_등록_되어있음("모란역").as(StationResponse.class);
        복정역 = 지하철역_등록_되어있음("복정역").as(StationResponse.class);
        강남역 = 지하철역_등록_되어있음("강남역").as(StationResponse.class);
        광교역 = 지하철역_등록_되어있음("광교역").as(StationResponse.class);
        분당선 = new LineRequest("분당선", "yellow", 모란역.getId(), 복정역.getId(), 10);
        신분당선 = new LineRequest("신분당선", "red", 강남역.getId(), 광교역.getId(), 20);
    }

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void createLine() {
        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(분당선);

        // then
        지하철_노선_생성됨(response);
    }

    @DisplayName("기존에 존재하는 지하철 노선 이름으로 지하철 노선을 생성한다.")
    @Test
    void createLine2() {
        // given
        지하철_노선_생성_요청(분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_생성_요청(분당선);

        // then
        지하철_노선_생성_실패됨(response);
    }

    @DisplayName("지하철 노선 목록을 조회한다.")
    @Test
    void getLines() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_목록_조회_요청();

        // then
        지하철_노선_목록_응답됨(response);
        지하철_노선_목록_포함됨(response, Arrays.asList(생성된_분당선, 생성된_신분당선));
    }

    @DisplayName("지하철 노선을 조회한다. (역 목록 포함)")
    @Test
    void getLine() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);

        // when
        Long 분당선_ID = Long.parseLong(생성된_분당선.header("Location").split("/")[2]);
        ExtractableResponse<Response> response = 지하철_노선_조회_요청(분당선_ID);

        // then
        지하철_노선_응답됨(response, 생성된_분당선);
        지하철_노선_지하철역_정렬된_목록_포함됨(response, Arrays.asList(모란역, 복정역));
    }

    @DisplayName("지하철 노선을 수정한다.")
    @Test
    void updateLine() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);
        LineRequest 수정내용 = 신분당선;

        // when
        ExtractableResponse<Response> 수정된_분당선 = 지하철_노선_수정_요청(수정내용, 생성된_분당선);

        // then
        지하철_노선_수정됨(수정된_분당선);
    }

    @DisplayName("지하철 노선을 제거한다.")
    @Test
    void deleteLine() {
        // given
        ExtractableResponse<Response> 생성된_분당선 = 지하철_노선_생성_요청(분당선);

        // when
        ExtractableResponse<Response> response = 지하철_노선_제거_요청(생성된_분당선);

        // then
        지하철_노선_삭제됨(response);
    }
}
