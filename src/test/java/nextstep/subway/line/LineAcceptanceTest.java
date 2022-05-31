package nextstep.subway.line;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.BasicAcceptance;
import nextstep.subway.domain.Station;
import nextstep.subway.dto.LineRequest;
import nextstep.subway.dto.LineResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nextstep.subway.station.StationAcceptanceTest.역_객체로_변환;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_생성;
import static nextstep.subway.utils.RequestUtil.요청_성공_실패_여부_확인;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DisplayName("노선 관련 기능")
public class LineAcceptanceTest extends BasicAcceptance {
    static final Long 등록되지_않은역_ID = 100L;
    static final Long 등록되지_않은_또다른역_ID = 200L;

    private Station 강남역;
    private Station 양재역;
    private Station 신사역;

    Long 강남역_ID;
    Long 양재역_ID;
    Long 신사역_ID;

    @Override
    protected void beforeEachInit() {
        강남역 = 역_객체로_변환(지하철역_생성("강남역"));
        양재역 = 역_객체로_변환(지하철역_생성(("양재역")));
        신사역 = 역_객체로_변환(지하철역_생성(("신사역")));
        강남역_ID = 강남역.getId();
        양재역_ID = 양재역.getId();
        신사역_ID = 신사역.getId();
    }

    /**
     * When  등록된 역이 없는 상태에서 노선을 저장한다.
     * Then 노선 저장이 실패 한다.
     */
    @DisplayName("등록된 역이 없으면 노선을 저장 할수 없다.")
    @Test
    void invalidCreateLineTest() {

        // Given
        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 등록되지_않은역_ID, 등록되지_않은_또다른역_ID, 10L);

        // When
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성(신분당선);

        // Then
        지하철_노선_생성_안됨(생성된_신분당선);
    }

    /**
     * When 등록된 역 정보를 이용하여 노선을 생성하면
     * Then 노선이 생성된다
     * Then 노선 조회 시 생성한 노선을 찾을 수 있다
     */

    @DisplayName("등록된 역 정보를 이용하여 노선을 저장한다.")
    @Test
    void createLineTest() {

        // Given
        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 양재역_ID, 10L);

        // When
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성(신분당선);

        // Then
        지하철_노선_생성됨(생성된_신분당선);
    }

    /**
     * Given 2개의 지하철 노선을 생성하고
     * When 지하철 노선 목록을 조회하면
     * Then 지하철 노선 목록 조회 시 2개의 노선을 조회할 수 있다.
     */
    @DisplayName("노선 2개 추가 후 노선을 조회한다.")
    @Test
    void addTwoLineAndFindAll() {
        // Given
        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 양재역_ID, 10L);
        final LineRequest 삼호선 = new LineRequest("삼호선", "bg-red-600", 양재역_ID, 신사역_ID, 10L);
        지하철_노선_생성됨(지하철_노선_생성(신분당선));
        지하철_노선_생성됨(지하철_노선_생성(삼호선));


        // When
        ExtractableResponse<Response> 정상처리된_요청 = 지하철_노선_검색이_정상처리됨(지하철_노선_전체_검색());

        // Then
        지하철_노선_검색됨(정상처리된_요청, "신분당선");
        지하철_노선_검색됨(정상처리된_요청, "삼호선");
    }


    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 조회하면
     * Then 생성한 지하철 노선의 정보를 응답받을 수 있다.
     */
    @DisplayName("노선 추가 후 노선 id로 조회한다.")
    @Test
    void addLineAndSearch() {
        // Given
        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 양재역_ID, 10L);
        LineResponse 신분당선_객체 = 객체로_변환(지하철_노선_생성됨(지하철_노선_생성(신분당선)));

        // When
        ExtractableResponse<Response> 검색_결과 = 지하철_노선_일부_검색(신분당선_객체.getId());
        지하철_노선_검색됨(검색_결과, "신분당선");

        // Then
        지하철_노선_검색이_정상처리됨(검색_결과);
        지하철_노선_검색됨(검색_결과, "신분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 수정하면
     * Then 해당 지하철 노선 정보는 수정된다
     */
    @DisplayName("노선 생성 후 수정하기")
    @Test
    void changeTest() {
        // Given
        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 양재역_ID, 10L);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성(신분당선);
        LineResponse 신분당선_객체 = 객체로_변환(생성된_신분당선);

        // When
        LineRequest 신분당선역_이름_교체_요청 = new LineRequest("다른분당선", "bg-red-600", 0L, 0L, 0L);
        ExtractableResponse<Response> 정보_업데이트_결과 = 지하철_노선_정보_업데이트(신분당선_객체.getId(), 신분당선역_이름_교체_요청);
        지하철_노선_정보가_업데이트됨(정보_업데이트_결과);

        // Then
        지하철_노선_정보가_업데이트됨(정보_업데이트_결과);
        ExtractableResponse<Response> 검색_결과 = 지하철_노선_일부_검색(신분당선_객체.getId());
        지하철_노선_검색됨(검색_결과, "다른분당선");
    }

    /**
     * Given 지하철 노선을 생성하고
     * When 생성한 지하철 노선을 삭제하면
     * Then 해당 지하철 노선 정보는 삭제된다
     */
    @DisplayName("노선 추가 후 삭제하기")
    @Test
    void addAndDeleteTest() {
        // Given
        final LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 양재역_ID, 10L);
        LineResponse 신분당선_객체 = 객체로_변환(지하철_노선_생성됨(지하철_노선_생성(신분당선)));

        // When
        지하철_노선_삭제(신분당선_객체.getId());

        // Then
        ExtractableResponse<Response> 검색_결과 = 지하철_노선_일부_검색(신분당선_객체.getId());
        지하철_노선_검색_요청_실패(검색_결과);
    }

    private ExtractableResponse<Response> 지하철_노선_정보_업데이트(final Long id, final LineRequest lineRequest) {
        return requestUtil.updateLine(id, convertMapBy(lineRequest));
    }

    private ExtractableResponse<Response> 지하철_노선_생성(final LineRequest lineRequest) {
        return requestUtil.createLine(convertMapBy(lineRequest));
    }

    private ExtractableResponse<Response> 지하철_노선_전체_검색() {
        return requestUtil.searchAllLine();
    }

    private ExtractableResponse<Response> 지하철_노선_일부_검색(final Long id) {
        return requestUtil.searchLine(id);
    }

    private ExtractableResponse<Response> 지하철_노선_삭제(final Long id) {
        return requestUtil.deleteLine(id);
    }

    private List<LineResponse> 객체리스트로_변환(ExtractableResponse<Response> response) {
        try {
            return response.jsonPath().getList(".", LineResponse.class);
        } catch (ClassCastException e) {
            return Collections.singletonList(response.as(LineResponse.class));
        }
    }

    private LineResponse 객체로_변환(ExtractableResponse<Response> response) {
        return response.as(LineResponse.class);
    }

    private boolean 지하철노선이름으로_검색(List<LineResponse> responseList, final String lineName) {
        return responseList.stream().anyMatch(lineResponse -> Objects.equals(lineResponse.getName(), lineName));
    }

    private Map<String, String> convertMapBy(final LineRequest lineRequest) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(lineRequest, Map.class);
    }

    private ExtractableResponse<Response> 지하철_노선_생성됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.CREATED);
        return response;
    }

    private void 지하철_노선_생성_안됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.BAD_REQUEST);
    }

    private ExtractableResponse<Response> 지하철_노선_검색이_정상처리됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.OK);
        return response;
    }

    private ExtractableResponse<Response> 지하철_노선_정보가_업데이트됨(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.OK);
        return response;
    }

    private void 지하철_노선_검색됨(ExtractableResponse<Response> response, final String findName) {
        assertThat(지하철노선이름으로_검색(객체리스트로_변환(response), findName)).isTrue();
    }

    private void 지하철_노선_검색_요청_실패(ExtractableResponse<Response> response) {
        요청_성공_실패_여부_확인(response, HttpStatus.BAD_REQUEST);
    }
}
