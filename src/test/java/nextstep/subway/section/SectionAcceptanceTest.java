package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.section.dto.SectionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

import static nextstep.subway.line.LineSteps.지하철_노선_생성_요청;
import static nextstep.subway.section.SectionSteps.지하철_구간_생성_요청;
import static nextstep.subway.station.StationSteps.지하철_역_생성_요청;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends AcceptanceTest {
    private static final Map<String, String> 강남역 = new HashMap<>();
    private static final Map<String, String> 양재역 = new HashMap<>();
    private static final Map<String, String> 판교역 = new HashMap<>();
    private static final Map<String, String> 정자역 = new HashMap<>();
    private static final Long 강남역_ID = 1L;
    private static final Long 양재역_ID = 2L;
    private static final Long 판교역_ID = 3L;
    private static final Long 정자역_ID = 4L;

    // 강남 - 양재 - 양재시민의숲 - 청계산입구 - 판교 - 정자 - 미금 - 동천 - 수지구청 - 성복 - 상현 - 광교중앙 - 광교
    @BeforeEach
    void init() {
        강남역.put("name", "강남역");
        양재역.put("name", "양재역");
        판교역.put("name", "판교역");
        정자역.put("name", "정자역");
        지하철_역_생성_요청(강남역);
        지하철_역_생성_요청(양재역);
        지하철_역_생성_요청(판교역);
        지하철_역_생성_요청(정자역);
    }

    @DisplayName("상행역과 하행역 사이에 새로운 역을 등록한다.")
    @Test
    void addSectionBetweenUpStationAndDownStation() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 20);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        Integer 신분당선_ID = 생성된_신분당선.jsonPath().get("id");
        SectionRequest sectionRequest = new SectionRequest(강남역_ID, 양재역_ID, 10);
        ExtractableResponse<Response> 생성된_구간 = 지하철_구간_생성_요청(sectionRequest, 신분당선_ID);

        // then
        assertThat(생성된_구간.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 역을 상행 종점으로 등록한다.")
    @Test
    void addSectionBeforeUpStation() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 양재역_ID, 판교역_ID, 10);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        Integer 신분당선_ID = 생성된_신분당선.jsonPath().get("id");
        SectionRequest sectionRequest = new SectionRequest(강남역_ID, 양재역_ID, 10);
        ExtractableResponse<Response> 생성된_구간 = 지하철_구간_생성_요청(sectionRequest, 신분당선_ID);

        // then
        assertThat(생성된_구간.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("새로운 역을 하행 종점으로 등록한다.")
    @Test
    void addSectionAfterDownStation() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 양재역_ID, 판교역_ID, 10);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        Integer 신분당선_ID = 생성된_신분당선.jsonPath().get("id");
        SectionRequest sectionRequest = new SectionRequest(판교역_ID, 정자역_ID, 10);
        ExtractableResponse<Response> 생성된_구간 = 지하철_구간_생성_요청(sectionRequest, 신분당선_ID);

        // then
        assertThat(생성된_구간.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @DisplayName("역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.")
    @Test
    void addSection_badRequest_case1() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 20);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        Integer 신분당선_ID = 생성된_신분당선.jsonPath().get("id");
        SectionRequest sectionRequest = new SectionRequest(강남역_ID, 양재역_ID, 20);
        ExtractableResponse<Response> 생성된_구간 = 지하철_구간_생성_요청(sectionRequest, 신분당선_ID);

        // then
        assertThat(생성된_구간.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.")
    @Test
    void addSection_badRequest_case2() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 20);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        Integer 신분당선_ID = 생성된_신분당선.jsonPath().get("id");
        SectionRequest sectionRequest = new SectionRequest(강남역_ID, 판교역_ID, 20);
        ExtractableResponse<Response> 생성된_구간 = 지하철_구간_생성_요청(sectionRequest, 신분당선_ID);

        // then
        assertThat(생성된_구간.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없음")
    @Test
    void addSection_badRequest_case3() {
        // given
        LineRequest 신분당선 = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 판교역_ID, 20);
        ExtractableResponse<Response> 생성된_신분당선 = 지하철_노선_생성_요청(신분당선);

        // when
        Integer 신분당선_ID = 생성된_신분당선.jsonPath().get("id");
        SectionRequest sectionRequest = new SectionRequest(양재역_ID, 정자역_ID, 20);
        ExtractableResponse<Response> 생성된_구간 = 지하철_구간_생성_요청(sectionRequest, 신분당선_ID);

        // then
        assertThat(생성된_구간.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
