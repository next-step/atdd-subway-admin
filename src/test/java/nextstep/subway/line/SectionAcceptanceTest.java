package nextstep.subway.line;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;

import static nextstep.subway.line.LineSteps.*;
import static nextstep.subway.line.SectionSteps.*;
import static nextstep.subway.station.StationSteps.지하철역_등록_되어있음;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {

    private StationResponse 강남역;
    private StationResponse 광교역;
    private StationResponse 판교역;
    private StationResponse 정자역;
    private LineRequest requestParams;
    private LineResponse 신분당선;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록_되어있음("강남역").as(StationResponse.class);
        광교역 = 지하철역_등록_되어있음("광교역").as(StationResponse.class);
        판교역 = 지하철역_등록_되어있음("판교역").as(StationResponse.class);
        정자역 = 지하철역_등록_되어있음("정자역").as(StationResponse.class);

        requestParams = new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 광교역.getId(), 10);
        신분당선 = 지하철_노선_등록되어_있음(requestParams).as(LineResponse.class);
    }

    @DisplayName("노선에 구간을 등록한다. (새로운 역을 하행 종점으로 등록할 경우)")
    @Test
    void addSection_at_after() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 광교역, 판교역, 10);

        ExtractableResponse<Response> 조회된_신분당선 = 지하철_노선_조회_요청(신분당선.getId());

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선_지하철역_정렬된_목록_포함됨(조회된_신분당선, Arrays.asList(강남역, 광교역, 판교역));
    }

    @DisplayName("노선에 구간을 등록한다. (새로운 역을 상행 종점으로 등록할 경우)")
    @Test
    void addSection_at_before() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 정자역, 강남역, 10);

        ExtractableResponse<Response> 조회된_신분당선 = 지하철_노선_조회_요청(신분당선.getId());

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선_지하철역_정렬된_목록_포함됨(조회된_신분당선, Arrays.asList(정자역, 강남역, 광교역));
    }

    @DisplayName("노선에 구간을 등록한다. (새로운 역을 기존 역사이에 등록할 경우[상행종점이 같을 때])")
    @Test
    void addSection_at_inside_same_upstation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 판교역, 5);

        ExtractableResponse<Response> 조회된_구간들 = 지하철_노선_조회_요청(신분당선.getId());

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선_지하철역_정렬된_목록_포함됨(조회된_구간들, Arrays.asList(강남역, 판교역, 광교역));
    }

    @DisplayName("노선에 구간을 등록한다. (새로운 역을 기존 역사이에 등록할 경우[하행종점이 같을 때])")
    @Test
    void addSection_at_inside_same_downstation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 정자역, 광교역, 5);

        ExtractableResponse<Response> 조회된_구간들 = 지하철_노선_조회_요청(신분당선.getId());

        // then
        지하철_노선에_구간_등록됨(response);
        지하철_노선_지하철역_정렬된_목록_포함됨(조회된_구간들, Arrays.asList(강남역, 정자역, 광교역));
    }

    @DisplayName("노선에 구간을 등록할 때 실패한다. (새로운 역을 기존 역사이에 등록할 때, 기존 구간 길이보다 같거나 클 경우")
    @ParameterizedTest
    @ValueSource(ints = {10, 11, 12})
    void addSection_at_inside_fail(int distance) {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 정자역, 광교역, distance);

        지하철_노선_구간_생성_실패됨(response);
    }

    @DisplayName("노선에 구간을 등록할 때 실패한다. (상행역, 하행역이 이미 노선에 등록되어 있다.)")
    @Test
    void addSection_fail_has_upstation_and_downstation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 강남역, 광교역, 5);

        // then
        지하철_노선_구간_생성_실패됨(response);
    }

    @DisplayName("노선에 구간을 등록할 때 실패한다. (상행역, 하행역이 어느것도 노선에 등록되어 있지 않다.)")
    @Test
    void addSection_fail_has_not_upstation_and_downstation() {
        // when
        ExtractableResponse<Response> response = 지하철_노선에_구간_등록_요청(신분당선, 판교역, 정자역, 5);

        // then
        지하철_노선_구간_생성_실패됨(response);
    }

    @DisplayName("노선의 구간을 제거한다. - 상행 종점이 제거될 경우 다음 구간 상행역이 상행 종점이 된다.")
    @Test
    void removeSection_at_boundary_upstation() {
        // given
        ExtractableResponse<Response> 구간_등록된_신분당선 = 지하철_노선에_구간_등록_요청(신분당선, 광교역, 판교역, 15);

        // when
        ExtractableResponse<Response> response = 지하철_노선_구간_삭제_요청(구간_등록된_신분당선, 강남역);
        ExtractableResponse<Response> 조회된_구간들 = 지하철_노선_조회_요청(신분당선.getId());

        지하철_노선_구간_삭제됨(response);
        지하철_노선_지하철역_정렬된_목록_포함됨(조회된_구간들, Arrays.asList(광교역, 판교역));
    }
}
